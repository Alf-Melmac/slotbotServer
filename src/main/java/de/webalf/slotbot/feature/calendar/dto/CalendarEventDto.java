package de.webalf.slotbot.feature.calendar.dto;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.GuildDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 24.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CalendarEventDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	private String title;

	@NonNull
	private LocalDateTime start;

	@NonNull
	private VisibleEventTypeDto eventType;

	@NonNull
	private GuildDto ownerGuild;

	private ShortEventInformationDto shortInformation;
}
