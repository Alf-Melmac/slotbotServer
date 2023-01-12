package de.webalf.slotbot.assembler.website.event.edit;

import de.webalf.slotbot.assembler.website.event.creation.MinimalEventFieldAssembler;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalEventFieldIdDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
final class MinimalEventFieldIdAssembler {
	private static MinimalEventFieldIdDto toDto(@NonNull EventField eventField) {
		MinimalEventFieldIdDto.MinimalEventFieldIdDtoBuilder<?, ?> builder = MinimalEventFieldIdDto.builder();
		MinimalEventFieldAssembler.toDto(builder, eventField);
		return builder
				.id(eventField.getId())
				.build();
	}

	static List<MinimalEventFieldIdDto> toDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(MinimalEventFieldIdAssembler::toDto)
				.toList();
	}
}
