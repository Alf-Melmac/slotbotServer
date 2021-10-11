package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventDetailsDto extends AbstractEventDto {
	String channelUrl;

	List<EventFieldReferencelessDto> details;

	List<EventDetailsSquadDto> squadList;
}
