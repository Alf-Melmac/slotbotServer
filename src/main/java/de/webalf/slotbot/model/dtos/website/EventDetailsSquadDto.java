package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.GuildDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.model.Squad.RESERVE_NAME;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventDetailsSquadDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	GuildDto reservedFor;

	List<EventDetailsSlotDto> slotList;

	boolean notEmpty;

	public boolean isReserve() {
		return getName().equals(RESERVE_NAME);
	}
}
