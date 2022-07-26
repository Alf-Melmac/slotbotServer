package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

@Value
@Builder
public class MinimalSquadDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	List<MinimalSlotDto> slotList;

	String reservedFor;
}
