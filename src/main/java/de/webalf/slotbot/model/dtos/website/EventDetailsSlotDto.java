package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.feature.slot_rules.SlottableDto;
import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.GuildDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

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

	/**
	 * <code>true</code> if a human is slotted on this slot
	 */
	boolean occupied;

	boolean blocked;

	/**
	 * <code>true</code> if the logged-in user is slotted on this slot
	 */
	boolean own;

	/**
	 * Usability of the slot for the logged-in user
	 */
	@NotNull
	SlottableDto slottable;
}
