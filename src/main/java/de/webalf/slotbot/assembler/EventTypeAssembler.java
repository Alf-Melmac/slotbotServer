package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 08.04.2021
 */
@UtilityClass
public final class EventTypeAssembler {
	public static EventType fromDto(EventTypeDto dto) {
		if (dto == null) {
			return null;
		}

		return EventType.builder()
				.id(dto.getId())
				.name(dto.getName())
				.color(dto.getColor())
				.build();
	}

	public static EventTypeDto toDto(EventType eventType) {
		return EventTypeDto.builder()
				.id(eventType.getId())
				.name(eventType.getName())
				.color(eventType.getColor())
				.build();
	}
}
