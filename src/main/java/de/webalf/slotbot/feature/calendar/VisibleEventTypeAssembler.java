package de.webalf.slotbot.feature.calendar;

import de.webalf.slotbot.feature.calendar.dto.VisibleEventTypeDto;
import de.webalf.slotbot.model.EventType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 21.10.25
 */
@UtilityClass
final class VisibleEventTypeAssembler {
	static VisibleEventTypeDto toDto(@NonNull EventType eventType) {
		return VisibleEventTypeDto.builder()
				.name(eventType.getName())
				.color(eventType.getColor())
				.build();
	}
}
