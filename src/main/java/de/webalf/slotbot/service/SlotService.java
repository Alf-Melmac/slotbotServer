package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotService {
	private final SlotRepository slotRepository;

	public Slot newSlot(SlotDto dto) {
		Slot slot = SlotAssembler.fromDto(dto);
		return slotRepository.save(slot);
	}

	private Slot updateSlot(SlotDto dto) {
		Slot slot = slotRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		//TODO Squad
//		DtoUtils.ifPresent(dto.getSquad(), slot::setSquad);
		DtoUtils.ifPresent(dto.getUserId(), slot::setUserIdString);

		return slot;
	}

	/**
	 * Saves the given Slot and writed directly to the database. Useful if the returned Slot is needed later in this transaction.
	 *
	 * @param dto to be saved
	 * @return the saved slot
	 */
	private Slot updateAndFlush(SlotDto dto) {
		return slotRepository.saveAndFlush(updateSlot(dto));
	}

	/**
	 * Slotts the given user to the given Slot
	 *
	 * @param slot   in which slot should be performed
	 * @param userId to be slotted
	 * @return the updated slot
	 */
	public Slot slot(Slot slot, long userId) {
		slot.slot(userId);
		return slotRepository.save(slot);
	}

	/**
	 * Removes the given user from the given slot
	 *
	 * @param slot   in which unslot should be performed
	 * @param userId to be unslotted
	 */
	public void unslot(Slot slot, long userId) {
		slot.unslot(userId);
		slotRepository.save(slot);
	}

	/**
	 * Removes the given Slot
	 *
	 * @param slot to remove
	 */
	public void deleteSlot(Slot slot) {
		slotRepository.delete(slot);
	}

	/**
	 * Swaps the users of the given slots
	 *
	 * @param slotDtos list with two slots from which the users should be swapped
	 * @return the event from the second slot
	 */
	public Event swap(List<SlotDto> slotDtos) {
		if (slotDtos.size() != 2) {
			throw BusinessRuntimeException.builder().title("Es können nur zwei Slots getauscht werden").build();
		}

		String tempUserId = slotDtos.get(0).getUserId();
		updateAndFlush(slotDtos.get(0).slot(slotDtos.get(1).getUserId()));
		Slot savedSlot2 = updateAndFlush(slotDtos.get(1).slot(tempUserId));

		return savedSlot2.getSquad().getEvent();
	}
}
