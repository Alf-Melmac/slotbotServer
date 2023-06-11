package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.website.UserNameDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

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
	@Size(max = TEXT)
	String name;

	int number;

	UserNameDto user;
}
