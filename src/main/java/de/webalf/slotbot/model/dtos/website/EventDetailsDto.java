package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.URL;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EventDetailsDto extends AbstractIdEntityDto {
	boolean hidden;

	@NotNull
	String ownerGuildIdentifier;

	@Size(max = TEXT)
	String missionType;

	@NotNull
	EventTypeDto eventType;

	@Size(max = URL)
	String pictureUrl;

	@NotBlank
	@Size(max = TEXT)
	String name;

	@Size(max = TEXT)
	String missionLength;

	LocalDateTime dateTime;

	String descriptionAsHtml;

	@NotBlank
	@Size(max = TEXT)
	String creator;

	List<EventDetailsSquadDto> squadList;

	List<EventFieldReferencelessDto> details;

	List<RequirementListDto> requirements;
}
