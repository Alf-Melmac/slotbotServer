package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static de.webalf.slotbot.util.MaxLength.COLOR_RGB;
import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 08.04.2021
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Value
@Builder
public class EventTypeDto {
	//The ID of this entity does not matter. The unique key is the name and the colour.
	//Entities are to be found and created using these values.

	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotBlank
	@Size(max = COLOR_RGB)
	String color;

	public String getColor() {
		return color.toLowerCase();
	}
}
