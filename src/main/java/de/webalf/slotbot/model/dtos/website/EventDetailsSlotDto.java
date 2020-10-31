package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class EventDetailsSlotDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = 80)
	String name;

	@NotEmpty
	@Getter //Used for ordering
	int number;

	String text;

	boolean blocked;

	@Builder
	public EventDetailsSlotDto(long id,
	                           String name,
	                           int number,
	                           String text,
	                           boolean blocked) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.text = text;
		this.blocked = blocked;
	}
}
