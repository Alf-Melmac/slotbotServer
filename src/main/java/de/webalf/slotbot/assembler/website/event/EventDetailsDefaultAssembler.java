package de.webalf.slotbot.assembler.website.event;

import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Alf
 * @since 03.01.2024
 */
@UtilityClass
public final class EventDetailsDefaultAssembler {
	public List<EventFieldDefaultDto> toDto(EventDetailsDefault eventDetailsDefault) {
		if (eventDetailsDefault == null) {
			return Collections.emptyList();
		}

		return eventDetailsDefault.getEventFieldDefaults().stream()
				.map(fieldDefault -> EventFieldDefaultDto.builder()
						.title(fieldDefault.getTitle())
						.type(fieldDefault.getType())
						.selection(fieldDefault.getSelection())
						.text(fieldDefault.getText())
						.build())
				.toList();
	}
}
