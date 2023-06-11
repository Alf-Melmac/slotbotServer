package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.enums.EventFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 27.04.2021
 */
@Value
@Builder
public class EventFieldDefaultDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	String title;

	@NonNull
	EventFieldType type;

	List<String> selection;

	@Size(max = EMBEDDABLE_VALUE)
	String text;
}