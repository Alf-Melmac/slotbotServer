package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSlotIdDto;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotService {
	private final SlotRepository slotRepository;
	private final GuildService guildService;
	private final UserService userService;
	private final ActionLogService actionLogService;

	/**
	 * Returns the slot with the given id
	 *
	 * @param id of the slot to get
	 * @return slot
	 * @throws ResourceNotFoundException if no slot with the given id exists
	 */
	Slot findById(long id) {
		return slotRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Creates a new {@link Slot} with the values from the given {@link SlotDto}
	 *
	 * @param dto values
	 * @return saved new slot
	 */
	Slot newSlot(SlotDto dto) {
		Slot slot = SlotAssembler.fromDto(dto);
		return slotRepository.save(slot);
	}

	/**
	 * Updates the slotlist of the given squad to the given slotlist
	 *
	 * @param slotList new slotlist
	 * @param squad    to update
	 */
	void updateSlotList(@NonNull List<MinimalSlotIdDto> slotList, @NonNull Squad squad) {
		List<Slot> squadSlots = squad.getSlotList();
		if (squadSlots != null) {
			squadSlots.clear();
		} else {
			squad.setSlotList(new ArrayList<>());
			squadSlots = squad.getSlotList();
		}

		List<Slot> eventSlotList = new ArrayList<>();
		slotList.forEach(slotDto -> eventSlotList.add(updateOrCreateSlot(slotDto, squad)));
		squadSlots.addAll(eventSlotList);
	}

	/**
	 * Updates a slot with the given values identified by its id. If no slot can be found, a new one is created.
	 * (!) Squad can not be changed
	 *
	 * @param dto   with new values
	 * @param squad is required when a new slot must be created
	 * @return updated Slot
	 */
	private Slot updateOrCreateSlot(@NonNull MinimalSlotIdDto dto, @NonNull Squad squad) {
		Slot slot = slotRepository.findById(dto.getId()).orElseGet(() -> Slot.builder().squad(squad).build());

		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		slot.setReservedFor(guildService.evaluateReservedFor(dto.getReservedFor()));
		if (dto.isBlocked()) {
			final String replacementText = dto.getReplacementText();
			blockSlot(slot, StringUtils.isNotEmpty(replacementText) ? replacementText.trim() : null);
		} else if (slot.isBlocked()) {
			slot.setUser(null);
			slot.setReplacementText(null);
		}

		return slot;
	}

	/**
	 * Slots the given user to the given Slot. If the user is already slotted, it is removed from the other slot
	 *
	 * @param slot in which slot should be performed
	 * @param user to be slotted
	 * @return the updated slot
	 */
	Slot slot(@NonNull Slot slot, User user) {
		slot.slot(user);
		actionLogService.logEventAction(LogAction.SLOT, slot.getEvent(), user);
		return slot;
	}

	/**
	 * Removes the given user from the given slot
	 *
	 * @param slot in which unslot should be performed
	 * @param user to be unslotted
	 */
	void unslot(@NonNull Slot slot, User user) {
		if (user == null) {
			throw BusinessRuntimeException.builder().title("Einen leeren Slot brauchst du nicht ausslotten").build();
		}
		slot.unslot(user);
		actionLogService.logEventAction(LogAction.UNSLOT, slot.getEvent(), user);
	}

	/**
	 * Renames the given slot
	 *
	 * @param slot     to rename
	 * @param slotName new name
	 */
	void renameSlot(@NonNull Slot slot, @NotBlank String slotName) {
		slot.setName(slotName);
	}

	/**
	 * Removes the given slot
	 *
	 * @param slot to remove
	 */
	void deleteSlot(@NonNull Slot slot) {
		Squad squad = slot.getSquad();
		squad.deleteSlot(slot);
	}

	/**
	 * Blocks the given slot and sets the replacement text
	 *
	 * @param slot            to block
	 * @param replacementText to be shown instead the user
	 */
	void blockSlot(@NonNull Slot slot, String replacementText) {
		slot.blockSlot(userService.find(User.DEFAULT_USER_ID), replacementText);
	}

	/**
	 * Swaps the users of the given slots
	 *
	 * @param slotDtos list with two slots from which the users should be swapped
	 * @return the event from the second slot
	 */
	public Event swap(@NonNull List<SlotDto> slotDtos) {
		if (slotDtos.size() != 2) {
			throw BusinessRuntimeException.builder().title("Es können nur zwei Slots getauscht werden").build();
		}

		Slot slot1 = slotRepository.findById(slotDtos.get(0).getId()).orElseThrow(ResourceNotFoundException::new);
		Slot slot2 = slotRepository.findById(slotDtos.get(1).getId()).orElseThrow(ResourceNotFoundException::new);
		slot1.swapUsers(slot2);

		actionLogService.logEventAction(LogAction.SWAP, slot1.getEvent(), slot1.getUser(), slot2.getUser());

		return slot1.getEvent();
	}

	/**
	 * Counts the number of slots of the given user before today
	 *
	 * @param user to count slots for
	 * @return number of participations
	 */
	public long countByUserBeforeToday(@NonNull User user) {
		return slotRepository.countByUserAndSquadEventDateTimeBefore(user, DateUtils.now());
	}
}
