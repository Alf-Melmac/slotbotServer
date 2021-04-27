package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Builder
@Value
public class CalendarEventDto {
	@NotBlank
	@Size(max = 80)
	String title;

	LocalDateTime start;

	@NotBlank
	@Size(max = 7)
	String color;

	String description;

	String url;
}
