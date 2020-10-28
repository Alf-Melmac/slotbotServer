package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Builder
@Value
public class CalendarEventDto {
	String title;

	LocalDateTime start;

	String description;

	String url;
}
