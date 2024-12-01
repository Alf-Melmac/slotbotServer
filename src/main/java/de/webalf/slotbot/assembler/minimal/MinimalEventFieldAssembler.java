package de.webalf.slotbot.assembler.minimal;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 25.07.2022
 */
@UtilityClass
public final class MinimalEventFieldAssembler {
	private static EventField fromDto(@NonNull MinimalEventFieldDto dto) {
		return EventField.builder()
				.title(dto.getTitle().trim())
				.text(dto.getText().trim())
				.build();
	}

	public static List<EventField> fromDtoIterable(Iterable<? extends MinimalEventFieldDto> dtos) {
		if (dtos == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(dtos.spliterator(), false)
				.map(MinimalEventFieldAssembler::fromDto)
				.toList();
	}

	static <C extends MinimalEventFieldDto, B extends MinimalEventFieldDto.MinimalEventFieldDtoBuilder<C, B>> B
	toDto(MinimalEventFieldDto.MinimalEventFieldDtoBuilder<C, B> builder, @NonNull EventField eventField) {
		return builder
				.title(eventField.getTitle())
				.text(eventField.getText());
	}

	private static MinimalEventFieldDto toDto(@NonNull EventField eventField) {
		return toDto(MinimalEventFieldDto.builder(), eventField).build();
	}

	public static List<MinimalEventFieldDto> toDtoList(Iterable<? extends EventField> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), false)
				.map(MinimalEventFieldAssembler::toDto)
				.toList();
	}
}
