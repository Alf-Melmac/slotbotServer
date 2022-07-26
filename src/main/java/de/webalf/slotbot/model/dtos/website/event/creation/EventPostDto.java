package de.webalf.slotbot.model.dtos.website.event.creation;

import de.webalf.slotbot.model.dtos.EventTypeDto;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.*;

@Value
@Builder
public class EventPostDto {
	Boolean hidden;

	Boolean shareable;

	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotNull
	LocalDate date;

	@NotNull
	LocalTime startTime;

	@NotBlank
	@Size(max = TEXT)
	String creator;

	@NotNull
	EventTypeDto eventType;

	@Size(max = EMBEDDABLE_DESCRIPTION)
	String description;

	@Size(max = TEXT)
	String missionType;

	@Size(max = TEXT)
	String missionLength;

	@Size(max = URL)
	String pictureUrl;

	List<MinimalEventFieldDto> details;

	List<MinimalSquadDto> squadList;

	Boolean reserveParticipating;
}
