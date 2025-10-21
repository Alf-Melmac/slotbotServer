package de.webalf.slotbot.feature.calendar;

import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.feature.calendar.dto.CalendarEventDto;
import de.webalf.slotbot.model.Event;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 24.10.2020
 */
@UtilityClass
final class CalendarEventAssembler {
	private static CalendarEventDto toDto(@NonNull Event event) {
		return CalendarEventDto.builder()
				.id(event.getId())
				.title(event.getName())
				.start(event.getDateTime())
				.eventType(VisibleEventTypeAssembler.toDto(event.getEventType()))
				.ownerGuild(GuildAssembler.toDto(event.getOwnerGuild()))
				.shortInformation(ShortEventInformationAssembler.toDto(event))
				.build();
	}

	static List<CalendarEventDto> toDtoList(Iterable<? extends Event> content) {
		return StreamSupport.stream(content.spliterator(), false)
				.map(CalendarEventAssembler::toDto)
				.toList();
	}
}
