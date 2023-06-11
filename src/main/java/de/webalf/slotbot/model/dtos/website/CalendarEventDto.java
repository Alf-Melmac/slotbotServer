package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.ShortEventInformationDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.ConstraintConstants.HEX_COLOR;
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

	private LocalDateTime start;

	@NotBlank
	@Size(min = HEX_COLOR, max = HEX_COLOR)
	private String color;

	private ShortEventInformationDto shortInformation;
}
