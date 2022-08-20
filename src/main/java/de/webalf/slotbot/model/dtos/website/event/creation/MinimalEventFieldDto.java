package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Data
@SuperBuilder
public class MinimalEventFieldDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private final String title;

	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	private final String text;
}
