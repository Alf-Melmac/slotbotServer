package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.website.UserNameDto;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Slot Dto that includes important fields needed to display a slotlist
 *
 * @author Alf
 * @since 22.02.2021
 */
@Builder
@Value
public class SlotApiViewDto {
	@NotBlank
	@Size(max = 80)
	String name;

	int number;

	UserNameDto user;
}
