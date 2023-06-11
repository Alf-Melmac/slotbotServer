package de.webalf.slotbot.model.dtos.website.event;

import de.webalf.slotbot.model.dtos.EventTypeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * Abstract dto for creating or editing events. Doesn't include details and squad list.
 *
 * @author Alf
 * @since 20.08.2022
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public abstract class EventActionDto {
	private Boolean hidden;

	private Boolean shareable;

	@NotBlank
	@Size(max = TEXT)
	private String name;

	@NotNull
	private LocalDateTime dateTime;

	@NotBlank
	@Size(max = TEXT)
	private String creator;

	@NotNull
	private EventTypeDto eventType;

	@Size(max = EMBEDDABLE_DESCRIPTION)
	private String description;

	@Size(max = TEXT)
	private String missionType;

	@Size(max = TEXT)
	private String missionLength;

	@Size(max = URL)
	private String pictureUrl;

	private Boolean reserveParticipating;
}
