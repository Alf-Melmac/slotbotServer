package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.TEXT;

@Value
@Builder
public class MinimalSlotDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	int number;

	String reservedFor;

	boolean blocked;

	@Size(max = TEXT)
	String replacementText;
}
