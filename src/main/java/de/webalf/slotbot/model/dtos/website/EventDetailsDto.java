package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;
import static de.webalf.slotbot.util.MaxLength.URL;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventDetailsDto extends AbstractIdEntityDto {
	boolean hidden;

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

	ZonedDateTime dateTimeZoned;

	String descriptionAsHtml;

	@NotBlank
	@Size(max = TEXT)
	String creator;

	List<EventDetailsSquadDto> squadList;

	List<EventFieldReferencelessDto> details;
}
