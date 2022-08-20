package de.webalf.slotbot.model.dtos.website.event;

import de.webalf.slotbot.model.dtos.EventTypeDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

import static de.webalf.slotbot.util.MaxLength.*;

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
	private LocalDate date;

	@NotNull
	private LocalTime startTime;

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
