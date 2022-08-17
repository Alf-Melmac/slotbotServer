package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

@Data
@Builder
public class MinimalSquadDto {
	@NotBlank
	@Size(max = TEXT)
	private final String name;

	private List<MinimalSlotDto> slotList;

	private final String reservedFor;
}
