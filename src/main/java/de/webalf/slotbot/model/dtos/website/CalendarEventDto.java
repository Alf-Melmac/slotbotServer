package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.ShortEventInformationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static de.webalf.slotbot.util.MaxLength.COLOR_RGB;
import static de.webalf.slotbot.util.MaxLength.TEXT;

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

	private LocalDateTime start;

	@NotBlank
	@Size(max = COLOR_RGB)
	private String color;

	private ShortEventInformationDto shortInformation;
}
