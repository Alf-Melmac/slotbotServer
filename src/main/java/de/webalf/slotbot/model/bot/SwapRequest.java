package de.webalf.slotbot.model.bot;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

import static de.webalf.slotbot.service.bot.command.event.Swap.requests;

/**
 * @author Alf
 * @since 15.01.2021
 */
@AllArgsConstructor
@Getter
public class SwapRequest {
	private final Slot requesterSlot;
	private final Slot foreignerSlot;

	private final long messageId;

	public User getRequester() {
		return getRequesterSlot().getUser();
	}

	public User getRequestedSlotUser() {
		return getForeignerSlot().getUser();
	}

	public static boolean containsRequester(User requester) {
		return requests.stream().anyMatch(request -> request.getRequester().equals(requester));
	}

	public static boolean containsMessageId(long messageId) {
		return requests.stream().anyMatch(request -> request.getMessageId() == messageId);
	}

	public static Optional<SwapRequest> findByRequesterAndRequested(User requester, User requestedSlotUser) {
		return requests.stream()
				.filter(swapRequest -> swapRequest.getRequester().equals(requester)
						&& swapRequest.getRequestedSlotUser().equals(requestedSlotUser))
				.findAny();
	}

	public static SwapRequest findByMessageId(long messageId) {
		return requests.stream().filter(swapRequest -> swapRequest.getMessageId() == messageId).findAny()
				.orElse(null);
	}
}