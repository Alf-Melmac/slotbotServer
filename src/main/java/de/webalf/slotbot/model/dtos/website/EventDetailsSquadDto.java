package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class EventDetailsSquadDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = 80)
	String name;

	List<EventDetailsSlotDto> slotList;

	@Builder
	public EventDetailsSquadDto(long id, String name, List<EventDetailsSlotDto> slotList) {
		this.id = id;
		this.name = name;
		this.slotList = slotList;
	}
}
