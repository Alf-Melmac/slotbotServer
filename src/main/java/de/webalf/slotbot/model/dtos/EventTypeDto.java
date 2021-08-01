package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.COLOR_RGB;
import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 08.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Value
@SuperBuilder
public class EventTypeDto extends AbstractIdEntityDto {
	//The ID of this entity does not matter. The unique key is the name and the colour.
	//Entities are to be found and created using these values.

	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotBlank
	@Size(max = COLOR_RGB)
	String color;

	public String getColor() {
		return color.toUpperCase();
	}
}
