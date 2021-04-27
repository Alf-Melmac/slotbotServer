package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.EventFieldDto;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 08.04.2021
 */
@UtilityClass
public final class EventFieldAssembler {
	public static EventField fromDto(EventFieldDto dto) {
		if (dto == null) {
			return null;
		}

		return EventField.builder()
				.id(dto.getId())
				.title(dto.getTitle())
				.text(dto.getText())
				.event(EventAssembler.fromDto(dto.getEvent()))
				.build();
	}

	static EventFieldReferencelessDto toReferencelessDto(EventField eventField) {
		return EventFieldReferencelessDto.builder()
				.id(eventField.getId())
				.title(eventField.getTitle())
				.text(eventField.getText())
				.build();
	}

	static List<EventField> fromDtoIterable(Iterable<? extends EventFieldDto> dtos) {
		if (dtos == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(dtos.spliterator(), false)
				.map(EventFieldAssembler::fromDto)
				.collect(Collectors.toList());
	}

	public static List<EventFieldReferencelessDto> toReferencelessDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(EventFieldAssembler::toReferencelessDto)
				.collect(Collectors.toList());
	}
}
