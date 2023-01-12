package de.webalf.slotbot.model.dtos.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * Squad Dto that includes important fields needed to display a slotlist
 *
 * @author Alf
 * @since 22.02.2021
 */
@Builder
@Value
public class SquadApiViewDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	List<SlotApiViewDto> slotList;
}
