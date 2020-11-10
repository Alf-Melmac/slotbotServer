package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
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
	private final UserService userService;
	private final ActionLogService actionLogService;

	Slot newSlot(SlotDto dto) {
		Slot slot = SlotAssembler.fromDto(dto);
		return slotRepository.save(slot);
	}

	List<Slot> updateSlotList(List<SlotDto> slotList) {
		List<Slot> eventSlotList = new ArrayList<>();
		slotList.forEach(slotDto -> eventSlotList.add(updateSlot(slotDto)));
		return eventSlotList;
	}

	/**
	 * Updates a slot with the given values identified by its id
	 * (!) Squad can not be changes
	 *
	 * @param dto with new values
	 * @return updated Slot
	 */
	private Slot updateSlot(@NonNull SlotDto dto) {
		Slot slot = slotRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		DtoUtils.ifPresent(dto.getUser(), slot::setUser);
		DtoUtils.ifPresent(dto.getReplacementText(), slot::setReplacementText);

		return slot;
	}

	/**
	 * Slots the given user to the given Slot. If the user is already slotted, it is removed from the other slot
	 *
	 * @param slot in which slot should be performed
	 * @param user to be slotted
	 * @return the updated slot
	 */
	public Slot slot(@NonNull Slot slot, User user) {
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
			//TODO warning instead of exception
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
		if (StringUtils.isEmpty(replacementText)) {
			replacementText = "Gesperrt";
		}
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

		return slot1.getEvent();
	}
}
