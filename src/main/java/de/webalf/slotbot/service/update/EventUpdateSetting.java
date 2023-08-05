package de.webalf.slotbot.service.update;

import de.webalf.slotbot.model.Event;
import lombok.Builder;

/**
 * Holds information about an event update and which parts of the event needs to be updated
 */
@Builder
record EventUpdateSetting(Event event, boolean embed, boolean slotlist) {}