package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 24.10.2020
 */
@UtilityClass
public final class CalendarEventAssembler {
	private static CalendarEventDto toDto(Event event) {
		return CalendarEventDto.builder()
				.id(event.getId())
				.title(event.getName())
				.start(event.getDateTime())
				.color(event.getEventType().getColor())
				.shortInformation(ShortEventInformationAssembler.toDto(event))
				.build();
	}

	public static List<CalendarEventDto> toDtoList(Iterable<? extends Event> content) {
		return StreamSupport.stream(content.spliterator(), false)
				.map(CalendarEventAssembler::toDto)
				.toList();
	}
}
