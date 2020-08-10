package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.util.LongUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
public final class EventAssembler {
	public static Event fromDto(EventDto eventDto) {
		if (eventDto == null) {
			return null;
		}

		return Event.builder()
				.id(eventDto.getId())
				.name(eventDto.getName())
				.dateTime(LocalDateTime.of(eventDto.getDate(), eventDto.getStartTime()))
				.description(eventDto.getDescription())
				.channel(LongUtils.parseLong(eventDto.getChannel()))
				.squadList(SquadAssembler.fromDtoList(eventDto.getSquadList()))
				.infoMsg(LongUtils.parseLong(eventDto.getInfoMsg()))
				.slotListMsg(LongUtils.parseLong(eventDto.getSlotListMsg()))
				.build();
	}

	public static EventDto toDto(Event event) {
		LocalDateTime dateTime = event.getDateTime();
		return EventDto.builder()
				.id(event.getId())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.description(event.getDescription())
				.channel(Long.toString(event.getChannel()))
				.squadList(SquadAssembler.toDtoList(event.getSquadList()))
				.infoMsg(Long.toString(event.getInfoMsg()))
				.slotListMsg(Long.toString(event.getSlotListMsg()))
				.build();
	}

	private static List<EventDto> toDtoList(List<Event> content) {
		return content.stream().map(EventAssembler::toDto).collect(Collectors.toList());
	}

	public static Page<EventDto> toDtoPage(Page<Event> eventPage, Pageable pageable) {
		List<EventDto> eventDtoList = toDtoList(eventPage.getContent());
		return new PageImpl<>(eventDtoList, pageable, eventPage.getTotalElements());
	}
}
