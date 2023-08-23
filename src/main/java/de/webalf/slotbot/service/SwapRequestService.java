package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.SwapRequest;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.enums.SwapRequestResult;
import de.webalf.slotbot.model.event.SwapRequestAcceptedEvent;
import de.webalf.slotbot.model.event.SwapRequestCreatedEvent;
import de.webalf.slotbot.model.event.SwapRequestDeclinedEvent;
import de.webalf.slotbot.repository.SwapRequestRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Alf
 * @since 22.08.2023
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SwapRequestService {
	private final SwapRequestRepository swapRequestRepository;
	private final SlotService slotService;
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;

	public SwapRequest findById(long swapRequestId) {
		return swapRequestRepository.findById(swapRequestId).orElseThrow(ResourceNotFoundException::new);
	}

	public SwapRequestResult swapByUsers(@NonNull Event event, long requesterId, long foreignId) {
		if (requesterId == foreignId) {
			return SwapRequestResult.ERROR_OWN_SLOT;
		}

		final User requester = userService.find(requesterId);
		final User foreign = userService.find(foreignId);
		final Slot requesterSlot = slotService.findByUserAndEvent(requester, event);
		final Slot foreignSlot = slotService.findByUserAndEvent(foreign, event);

		if (swapRequestRepository.existsByRequesterSlotAndForeignSlot(requesterSlot, foreignSlot)) {
			return SwapRequestResult.ERROR_PENDING;
		}
		final Optional<SwapRequest> reversedRequest = swapRequestRepository.findByRequesterSlotAndForeignSlot(foreignSlot, requesterSlot);
		if (reversedRequest.isPresent()) {
			performSwap(reversedRequest.get());
			return SwapRequestResult.SUCCESS;
		}

		final SwapRequest swapRequest = swapRequestRepository.save(SwapRequest.builder()
				.requesterSlot(requesterSlot)
				.foreignSlot(foreignSlot)
				.build());
		eventPublisher.publishEvent(SwapRequestCreatedEvent.builder()
				.swapRequestId(swapRequest.getId())
				.requester(requester)
				.requesterSlot(requesterSlot)
				.event(event)
				.foreign(foreign)
				.foreignSlot(foreignSlot)
				.build());
		return SwapRequestResult.SUCCESS;
	}

	public void addMessageId(long swapRequestId, long messageId) {
		swapRequestRepository.updateMessageIdById(messageId, swapRequestId);
	}

	public void performSwap(@NonNull SwapRequest swapRequest) {
		slotService.swap(swapRequest.getRequesterSlot(), swapRequest.getForeignSlot());
		swapRequestRepository.delete(swapRequest);
		eventPublisher.publishEvent(SwapRequestAcceptedEvent.builder()
				.messageId(swapRequest.getMessageId())
				.requestedUserId(swapRequest.getRequesterSlot().getUser().getId())
				.build());
	}

	public void declineSwap(@NonNull SwapRequest swapRequest) {
		eventPublisher.publishEvent(SwapRequestDeclinedEvent.builder()
				.swapRequestId(swapRequest.getId())
				.requesterUserId(swapRequest.getRequesterSlot().getUser().getId())
				.foreignUserId(swapRequest.getForeignSlot().getUser().getId())
				.messageId(swapRequest.getMessageId())
				.locale(swapRequest.getRequesterSlot().getEvent().getOwnerGuildLocale())
				.build());
	}

	@EventListener
	@Async
	public void swapRequestDeclined(@NonNull SwapRequestDeclinedEvent declinedEvent) {
		swapRequestRepository.deleteById(declinedEvent.swapRequestId());
	}
}
