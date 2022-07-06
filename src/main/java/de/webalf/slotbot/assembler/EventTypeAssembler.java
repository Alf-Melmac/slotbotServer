package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
				.name(dto.getName())
				.color(dto.getColor())
				.build();
	}

	public static EventTypeDto toDto(EventType eventType) {
		return EventTypeDto.builder()
				.name(eventType.getName())
				.color(eventType.getColor())
				.build();
	}

	public static List<EventTypeDto> toDtoList(Iterable<? extends EventType> eventTypes) {
		return StreamSupport.stream(eventTypes.spliterator(), false)
				.map(EventTypeAssembler::toDto)
				.collect(Collectors.toList());
	}
}
