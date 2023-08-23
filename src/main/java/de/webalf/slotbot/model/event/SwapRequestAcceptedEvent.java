package de.webalf.slotbot.model.event;

import lombok.Builder;

/**
 * Event to notify about an accepted and performed swap request
 *
 * @param requestedUserId user the swap was requested from
 * @param messageId       message that was used to accept the swap
 * @author Alf
 * @since 22.08.2023
 */
@Builder
public record SwapRequestAcceptedEvent(long requestedUserId,
                                       long messageId) {}
