package de.webalf.slotbot.model.dtos.api;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
