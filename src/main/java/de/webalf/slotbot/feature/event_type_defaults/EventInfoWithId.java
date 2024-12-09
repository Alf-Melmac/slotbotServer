package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;

/**
 * @author Alf
 * @since 03.12.2024
 */
public record EventInfoWithId(long id, EventType eventType, Guild ownerGuild) {
}
