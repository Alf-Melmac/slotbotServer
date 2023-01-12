package de.webalf.slotbot.model.dtos.website.event.creation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class MinimalEventFieldDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private final String title;

	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	private final String text;
}
