package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.repository.SlotRepository;
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

	public void slot(Slot slot, long userId) {
		slot.slot(userId);
		slotRepository.save(slot);
	}

	public void unslot(Slot slot, long userId) {
		slot.unslot(userId);
		slotRepository.save(slot);
	}
}
