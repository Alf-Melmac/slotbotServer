package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.HEX_COLOR;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 08.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventTypeDto extends AbstractIdEntityDto {
	//Former implementation ignored the id of the entity. Most places use the name and color to identify the entity.

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
