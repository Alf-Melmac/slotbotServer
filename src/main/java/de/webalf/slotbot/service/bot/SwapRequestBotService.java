package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.feature.swap.SwapRequestResult;
import de.webalf.slotbot.feature.swap.SwapRequestService;
import de.webalf.slotbot.feature.swap.model.SwapRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for {@link SwapRequestService} to be used by discord bot
 *
 * @author Alf
 * @since 22.08.2023
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SwapRequestBotService {
	private final SwapRequestService swapRequestService;
	private final EventBotService eventBotService;

	private SwapRequest findById(long swapRequestId) {
		return swapRequestService.findById(swapRequestId);
	}

	public SwapRequestResult swapByUsers(long channel, long requesterId, long foreignId) {
		return swapRequestService.swapByUsers(eventBotService.findByChannelOrThrow(channel), requesterId, foreignId);
	}

	public void accept(long swapRequestId) {
		swapRequestService.performSwap(findById(swapRequestId));
	}

	public void decline(long swapRequestId) {
		swapRequestService.declineSwap(findById(swapRequestId));
	}
}
