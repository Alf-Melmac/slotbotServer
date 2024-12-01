package de.webalf.slotbot.assembler.minimal;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldIdDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
public final class MinimalEventFieldIdAssembler {
	private static MinimalEventFieldIdDto toDto(@NonNull EventField eventField) {
		return MinimalEventFieldAssembler.toDto(MinimalEventFieldIdDto.builder(), eventField)
				.id(eventField.getId())
				.build();
	}

	public static List<MinimalEventFieldIdDto> toDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(MinimalEventFieldIdAssembler::toDto)
				.toList();
	}
}
