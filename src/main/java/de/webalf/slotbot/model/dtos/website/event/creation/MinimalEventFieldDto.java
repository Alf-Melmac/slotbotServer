package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_VALUE;

@Value
@Builder
public class MinimalEventFieldDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	String title;

	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	String text;
}
