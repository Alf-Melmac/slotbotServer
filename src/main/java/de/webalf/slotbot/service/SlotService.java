package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.*;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSlotIdDto;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.model.event.BanEvent;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SlotService {
	private final SlotRepository slotRepository;
	private final GuildService guildService;
	private final UserService userService;
	private final ActionLogService actionLogService;
	private final BanService banService;

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
	 * Returns the slot of the given user in the given event.
	 *
	 * @param user  user to find slot for
	 * @param event event in which the user may be slotted
	 * @return slot in the event in which the user is slotted
	 * @throws ResourceNotFoundException if no slot matching the user and event is found
	 */
	Slot findByUserAndEvent(@NonNull User user, @NonNull Event event) {
		return slotRepository.findByUserAndSquadEvent(user, event).orElseThrow(ResourceNotFoundException::new);
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

	public Boolean isSlottable(@NonNull Slot slot) {
		final User loggedIn = userService.getPotentialLoggedIn();
		final Event slotEvent = slot.getEvent();
		final Boolean slottable = loggedIn == null || !DateUtils.isInFuture(slotEvent.getDateTime())
				? null
				: slot.slotIsPossible(loggedIn);
		if (TRUE.equals(slottable) && banService.isBanned(loggedIn, slotEvent.getOwnerGuild(), slot.getEffectiveReservedFor())) {
			return null;
		}
		return slottable;
	}

	/**
	 * Slots the given user to the given Slot. If the user is already slotted, it is removed from the other slot
	 *
	 * @param slotId id of slot in which slot should be performed
	 * @param user   to be slotted
	 * @return the updated slot
	 */
	Slot slot(long slotId, User user) {
		final Slot slot = findById(slotId);
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
	 * Renames the slot with the given name for the specified event and slot number.
	 *
	 * @param event      event to which the slot belongs
	 * @param slotNumber slot number
	 * @param slotName   new slot name
	 */
	public void renameSlot(@NonNull Event event, int slotNumber, @NotBlank String slotName) {
		final Slot slot = slotRepository.findByNumberAndSquadEvent(slotNumber, event).orElseThrow(ResourceNotFoundException::new);
		slot.setName(slotName);
	}

	/**
	 * Removes the slot with the given slot number from the given event.
	 *
	 * @param event      event to which the slot belongs
	 * @param slotNumber slot number
	 */
	public void deleteSlot(@NonNull Event event, int slotNumber) {
		final Slot slot = slotRepository.findByNumberAndSquadEvent(slotNumber, event).orElseThrow(ResourceNotFoundException::new);
		slot.getSquad().deleteSlot(slot);
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
	 * @return the event from the second slot
	 */
	public Event swap(@NonNull Slot slot1, @NonNull Slot slot2) {
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

	@EventListener
	@Async
	public void onBanEvent(@NonNull BanEvent event) {
		final User user = userService.findExisting(event.userId());
		final List<Slot> slotsToBeEmptied;
		if (event.guildId() == null) {
			slotsToBeEmptied = slotRepository.findByUserAndEventAfter(user, DateUtils.now());
		} else {
			final Guild guild = guildService.findExisting(event.guildId());
			slotsToBeEmptied = slotRepository.findByUserAndForGuildAndEventAfter(user, guild, DateUtils.now());
		}
		slotsToBeEmptied.forEach(slot -> unslot(slot, user));
	}
}
