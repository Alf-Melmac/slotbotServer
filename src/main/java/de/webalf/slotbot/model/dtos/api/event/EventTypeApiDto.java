package de.webalf.slotbot.model.dtos.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import static de.webalf.slotbot.util.MaxLength.COLOR_RGB;
import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 08.06.2023
 */
@Value
@Builder
public class EventTypeApiDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotBlank
	@Size(max = COLOR_RGB)
	String color;
}
