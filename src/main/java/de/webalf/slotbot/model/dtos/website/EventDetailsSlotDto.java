package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.GuildDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

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

	GuildDto reservedFor;

	String text;

	boolean occupied;

	boolean blocked;
}
