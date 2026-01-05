package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 08.04.2021
 */
@UtilityClass
public final class EventFieldAssembler {
	private static EventFieldReferencelessDto toReferencelessDto(EventField eventField) {
		return EventFieldReferencelessDto.builder()
				.id(eventField.getId())
				.title(eventField.getTitle())
				.textAsHtml(eventField.getText())
				.build();
	}

	public static List<EventFieldReferencelessDto> toReferencelessDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(EventFieldAssembler::toReferencelessDto)
				.collect(Collectors.toList());
	}
}
