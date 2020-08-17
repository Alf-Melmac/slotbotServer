package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotService {
	private final SlotRepository slotRepository;

	public Slot newSlot(SlotDto dto) {
		Slot slot = new Slot();
		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		//TODO Squad
//		DtoUtils.ifPresent(dto.getSquad(), slot::setSquad);
		DtoUtils.ifPresent(dto.getUserId(), slot::setUserIdString);

		return slotRepository.save(slot);
	}

	public void slot(Slot slot, long userId) {
		slot.slot(userId);
		slotRepository.save(slot);
	}

	public void unslot(Slot slot, long userId) {
		slot.unslot(userId);
		slotRepository.save(slot);
	}

	public void deleteSlot(Slot slot) {
		slotRepository.delete(slot);
	}
}
