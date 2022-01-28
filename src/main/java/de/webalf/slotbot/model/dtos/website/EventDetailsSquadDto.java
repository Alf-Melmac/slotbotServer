package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.model.Squad.RESERVE_NAME;
import static de.webalf.slotbot.util.MaxLength.TEXT;

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

	Long reservedFor;

	List<EventDetailsSlotDto> slotList;

	boolean notEmpty;

	public boolean isReserve() {
		return getName().equals(RESERVE_NAME);
	}
}
