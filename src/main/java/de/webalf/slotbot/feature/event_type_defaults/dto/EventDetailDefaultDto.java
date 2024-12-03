package de.webalf.slotbot.feature.event_type_defaults.dto;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.enums.EventDetailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 27.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventDetailDefaultDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	String title;

	@NonNull
	EventDetailType type;

	List<String> selection;

	@Size(max = EMBEDDABLE_VALUE)
	String text;
}
