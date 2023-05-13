package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import lombok.Builder;

/**
 * Holds information about an event update and which parts of the event needs to be updated
 */
@Builder
public record EventUpdateSetting(Event event, boolean embed, boolean slotlist) {}