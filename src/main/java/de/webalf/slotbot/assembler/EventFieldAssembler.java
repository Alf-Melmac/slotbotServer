package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.dtos.EventFieldDto;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import de.webalf.slotbot.model.enums.EventFieldType;
import de.webalf.slotbot.util.eventfield.EventFieldUtils;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.eventfield.EventFieldUtils.buildOptionalLink;
import static de.webalf.slotbot.util.eventfield.EventFieldUtils.getDefaultSelection;

/**
 * @author Alf
 * @since 08.04.2021
 */
@UtilityClass
public final class EventFieldAssembler {
	private static EventField fromDto(EventFieldDto dto) {
		if (dto == null) {
			return null;
		}

		return EventField.builder()
				.id(dto.getId())
				.title(dto.getTitle().trim())
				.text(dto.getText().trim())
				.event(EventAssembler.fromDto(dto.getEvent()))
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

	private static EventFieldReferencelessDto toReferencelessDto(EventField eventField) {
		return EventFieldReferencelessDto.builder()
				.id(eventField.getId())
				.title(eventField.getTitle())
				.text(eventField.getText())
				.link(buildOptionalLink(eventField))
				.build();
	}

	public static List<EventFieldReferencelessDto> toReferencelessDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(EventFieldAssembler::toReferencelessDto)
				.collect(Collectors.toList());
	}

	private static EventFieldDefaultDto toDefaultDto(EventField eventField) {
		final EventFieldType fieldType = EventFieldUtils.getDefaultFieldType(eventField);
		return EventFieldDefaultDto.builder()
				.id(eventField.getId())
				.title(eventField.getTitle())
				.type(fieldType)
				.selection(getDefaultSelection(fieldType, eventField))
				.text(eventField.getText())
				.build();
	}

	public static List<EventFieldDefaultDto> toDefaultDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(EventFieldAssembler::toDefaultDto)
				.collect(Collectors.toList());
	}
}
