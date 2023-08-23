package de.webalf.slotbot.model.event;

import lombok.Builder;
import lombok.NonNull;

import java.util.Locale;

/**
 * Event to notify about a declined swap request
 *
 * @author Alf
 * @since 22.08.2023
 */
@Builder
public record SwapRequestDeclinedEvent(long swapRequestId,
                                       long requesterUserId,
                                       long foreignUserId,
                                       Long messageId,
                                       @NonNull Locale locale) {}
