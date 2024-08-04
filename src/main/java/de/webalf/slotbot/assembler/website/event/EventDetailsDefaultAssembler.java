package de.webalf.slotbot.assembler.website.event;

import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.dtos.EventDetailDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Alf
 * @since 03.01.2024
 */
@UtilityClass
public final class EventDetailsDefaultAssembler {
	public List<EventDetailDefaultDto> toDto(EventDetailsDefault eventDetailsDefault) {
		if (eventDetailsDefault == null) {
			return Collections.emptyList();
		}

		return eventDetailsDefault.getEventFieldDefaults().stream()
				//Introducing the id required to type the map method. Not sure why
				.<EventDetailDefaultDto>map(detailDefault -> EventDetailDefaultDto.builder()
						.id(detailDefault.getId())
						.title(detailDefault.getTitle())
						.type(detailDefault.getType())
						.selection(detailDefault.getSelection())
						.text(detailDefault.getText())
						.build())
				.toList();
	}
}
