package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Wrapper for {@link SlotService} to be used by discord bot
 *
 * @author Alf
 * @since 14.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotBotService {
	private final SlotService slotService;

	public void performDetachedSwap(Slot requesterSlot, Slot foreignSlot) {
		slotService.swap(SlotAssembler.toDtoList(List.of(requesterSlot, foreignSlot)));
	}
}
