package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	@Size(max = 80)
	String name;

	@NotBlank
	@Size(max = 7)
	String color;

	public String getColor() {
		return color.toUpperCase();
	}
}
