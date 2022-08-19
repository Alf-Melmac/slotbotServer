package de.webalf.slotbot.assembler.website.event.creation;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalEventFieldDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 25.07.2022
 */
@UtilityClass
final class MinimalEventFieldAssembler {
	private static EventField fromDto(MinimalEventFieldDto dto) {
		if (dto == null) {
			return null;
		}

		return EventField.builder()
				.title(dto.getTitle().trim())
				.text(dto.getText().trim())
				.build();
	}

	static List<EventField> fromDtoIterable(Iterable<? extends MinimalEventFieldDto> dtos) {
		if (dtos == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(dtos.spliterator(), false)
				.map(MinimalEventFieldAssembler::fromDto)
				.collect(Collectors.toList());
	}

	private static MinimalEventFieldDto toDto(EventField eventField) {
		return MinimalEventFieldDto.builder()
				.title(eventField.getTitle())
				.text(eventField.getText())
				.build();
	}

	public static List<MinimalEventFieldDto> toDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(MinimalEventFieldAssembler::toDto)
				.collect(Collectors.toList());
	}
}
