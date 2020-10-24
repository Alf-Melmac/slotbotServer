package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 24.10.2020
 */
public class CalendarEventAssembler {
	private static CalendarEventDto toDto(Event event) {
		return CalendarEventDto.builder()
				.title(event.getName())
				.start(event.getDateTime())
				.description(event.getDescription())
				.build();
	}

	public static List<CalendarEventDto> toDtoList(Iterable<? extends Event> content) {
		return StreamSupport.stream(content.spliterator(), false)
				.map(CalendarEventAssembler::toDto)
				.collect(Collectors.toList());
	}
}
