package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventDetailsSlotDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	int number;

	String text;

	boolean occupied;

	boolean blocked;
}
