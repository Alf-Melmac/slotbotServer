package de.webalf.slotbot.feature.swap.event;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import lombok.Builder;
import lombok.NonNull;

/**
 * Event after creation of a new swap request
 *
 * @author Alf
 * @since 22.08.2023
 */
@Builder
public record SwapRequestCreatedEvent(long swapRequestId,
                                      @NonNull User requester,
                                      @NonNull Slot requesterSlot,
                                      @NonNull Event event,
                                      @NonNull User foreign,
                                      @NonNull Slot foreignSlot) {}
