package de.webalf.slotbot.model.dtos.api.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 08.06.2023
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public abstract class EventActionApiDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotNull
	@Schema(example = "2023-06-14T19:00")
	LocalDateTime dateTime;

	@NotBlank
	@Size(max = TEXT)
	String creator;

	@NotNull
	EventTypeApiDto eventType;

	@Size(max = EMBEDDABLE_DESCRIPTION)
	String description;

	@Size(max = TEXT)
	String missionType;

	@Size(max = TEXT)
	String missionLength;

	@Size(max = URL)
	@Pattern(regexp = URL_PATTERN)
	@Schema(example = "https://example.net/example.png")
	String pictureUrl;

	Boolean reserveParticipating;
}
