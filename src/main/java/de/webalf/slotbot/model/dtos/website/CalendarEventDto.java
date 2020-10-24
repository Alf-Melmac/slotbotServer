package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Builder
@Getter
public class CalendarEventDto {
	private String title;

	private LocalDateTime start;

	private String description;
}
