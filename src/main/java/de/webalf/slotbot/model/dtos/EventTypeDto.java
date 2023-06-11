package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static de.webalf.slotbot.util.ConstraintConstants.HEX_COLOR;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

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
	@Size(min = HEX_COLOR, max = HEX_COLOR)
	String color;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	String guild;

	public String getColor() {
		return color.toLowerCase();
	}
}
