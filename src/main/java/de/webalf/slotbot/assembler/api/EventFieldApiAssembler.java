package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.api.EventFieldApiDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.EventUtils.buildOptionalLink;

/**
 * @author Alf
 * @since 14.04.2021
 */
@UtilityClass
public final class EventFieldApiAssembler {
	private static EventFieldApiDto toDto(EventField eventField) {
		return EventFieldApiDto.builder()
				.id(eventField.getId())
				.title(eventField.getTitle())
				.text(eventField.getText())
				.link(buildOptionalLink(eventField))
				.build();
	}

	public static List<EventFieldApiDto> toDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(EventFieldApiAssembler::toDto)
				.collect(Collectors.toList());
	}
}
