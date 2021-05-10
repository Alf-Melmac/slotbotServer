package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 10.05.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventEditDto extends AbstractEventDto {
	String channelUrl;

	List<EventDetailsSquadDto> squadList;

	List<EventFieldDefaultDto> details;
}
