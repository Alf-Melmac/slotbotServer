package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.ShortEventInformationDto;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static de.webalf.slotbot.util.MaxLength.COLOR_RGB;
import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Builder
@Value
public class CalendarEventDto {
	@NotBlank
	@Size(max = TEXT)
	String title;

	LocalDateTime start;

	@NotBlank
	@Size(max = COLOR_RGB)
	String color;

	ShortEventInformationDto shortInformation;

	String url;
}
