package de.webalf.slotbot.model.dtos.api.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 08.06.2023
 */
@Value
@Builder
public class EventTypeApiDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotBlank
	@Size(min = HEX_COLOR, max = HEX_COLOR)
	@Pattern(regexp = HEX_COLOR_PATTERN)
	@Schema(format = "hex-color")
	String color;
}
