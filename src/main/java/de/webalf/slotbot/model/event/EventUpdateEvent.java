package de.webalf.slotbot.model.event;

import lombok.Builder;

/**
 * Event for any changes to an event.
 *
 * @param slotlistChanged marks if the slotlist changed
 * @author Alf
 * @since 05.08.2023
 */
@Builder
public record EventUpdateEvent(long event, boolean embedChanged, boolean slotlistChanged) {}
