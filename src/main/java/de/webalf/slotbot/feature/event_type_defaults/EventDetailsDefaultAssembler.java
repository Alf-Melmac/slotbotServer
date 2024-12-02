package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.feature.event_type_defaults.dto.EventDetailDefaultDto;
import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailDefault;
import de.webalf.slotbot.model.EventDetailsDefault;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Alf
 * @since 03.01.2024
 */
@UtilityClass
final class EventDetailsDefaultAssembler {
	List<EventDetailDefaultDto> toDto(EventDetailsDefault eventDetailsDefault) {
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

	List<EventDetailDefaultDto> toDto(List<EventDetailDefault> eventDetailDefaults) {
		return eventDetailDefaults.stream()
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
