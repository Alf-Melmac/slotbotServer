package de.webalf.slotbot.feature.calendar.dto;

import de.webalf.slotbot.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static de.webalf.slotbot.util.ConstraintConstants.HEX_COLOR;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * Visible parts of an {@link EventType}
 *
 * @author Alf
 * @since 21.10.25
 */
@Builder
public record VisibleEventTypeDto(
		@NotBlank @Size(max = TEXT) String name,
		@NotBlank @Size(min = HEX_COLOR, max = HEX_COLOR) String color
) {}
