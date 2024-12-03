package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import lombok.Data;

/**
 * @author Alf
 * @since 03.12.2024
 */
@Data
public class EventInfoWithId {
	private final long id;
	private EventType eventType;
	private final Guild ownerGuild;
}
