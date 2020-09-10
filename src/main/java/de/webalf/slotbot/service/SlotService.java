package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private final ActionLogService actionLogService;
	private final SquadService squadService;

	Slot newSlot(SlotDto dto) {
		Slot slot = SlotAssembler.fromDto(dto);
		return slotRepository.save(slot);
	}

	private Slot updateSlot(@NonNull SlotDto dto) {
		Slot slot = slotRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		//TODO Squad
//		DtoUtils.ifPresent(dto.getSquad(), slot::setSquad);
		DtoUtils.ifPresent(dto.getUser(), slot::setUser);

		return slot;
	}

	/**
	 * Saves the given Slot
	 * Be aware that no squad change can be saved atm see {@link SlotService#updateSlot(SlotDto)}
	 *
	 * @param dto to be saved
	 * @return the saved slot
	 */
	private Slot updateAndSave(SlotDto dto) {
		return slotRepository.save(updateSlot(dto));
	}

	/**
	 * Slotts the given user to the given Slot. If the user is already slotted, it is removed from the other slot
	 *
	 * @param slot in which slot should be performed
	 * @param user to be slotted
	 * @return the updated slot
	 */
	public Slot slot(@NonNull Slot slot, User user) {
		//Remove the user from any other slot in the Event
		Event event = slot.getEvent();
		event.findSlotOfUser(user).ifPresent(alreadySlottedSlot -> {
			if (slot.equals(alreadySlottedSlot)) {
				//TODO: Return a warning, not a exception
				throw BusinessRuntimeException.builder().title("Die Person ist bereits auf diesem Slot").build();
			}
			unslot(alreadySlottedSlot, user);
		});

		slot.slot(user);
		actionLogService.logEventAction(LogAction.SLOT, event, user);
		return slotRepository.save(slot);
	}

	/**
	 * Removes the given user from the given slot
	 *
	 * @param slot in which unslot should be performed
	 * @param user to be unslotted
	 */
	void unslot(@NonNull Slot slot, User user) {
		slot.unslot(user);
		actionLogService.logEventAction(LogAction.UNSLOT, slot.getEvent(), user);
		slotRepository.save(slot);
	}

	/**
	 * Removes the given Slot
	 *
	 * @param slot to remove
	 */
	void deleteSlot(Slot slot) {
		if (slot.isNotEmpty()) {
			throw new ForbiddenException("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.");
		}
		Squad squad = slot.getSquad();
		squad.deleteSlot(slot);
		slotRepository.delete(slot);

		squadService.deleteSquadIfEmpty(squad);
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

		UserDto tempUser = slotDtos.get(0).getUser();
		updateAndSave(slotDtos.get(0).slot(slotDtos.get(1).getUser()));
		Slot savedSlot2 = updateAndSave(slotDtos.get(1).slot(tempUser));

		return savedSlot2.getEvent();
	}
}
