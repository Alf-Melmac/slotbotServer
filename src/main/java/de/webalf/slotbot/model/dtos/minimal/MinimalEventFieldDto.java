package de.webalf.slotbot.model.dtos.minimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MinimalEventFieldDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private String title;

	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	private String text;
}
