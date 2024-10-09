package de.webalf.slotbot.assembler.api.event;

import de.webalf.slotbot.assembler.minimal.MinimalEventFieldAssembler;
import de.webalf.slotbot.assembler.minimal.MinimalEventFieldIdAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.api.event.creation.EventApiDto;
import de.webalf.slotbot.model.dtos.api.event.view.EventApiIdDto;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.api.GuildApiService;
import de.webalf.slotbot.util.EventUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.BooleanUtils.parseBoolean;
import static de.webalf.slotbot.util.StringUtils.trim;

/**
 * @author Alf
 * @since 04.11.2020
 */
@Component
@RequiredArgsConstructor
public final class EventApiAssembler {
	private final EventTypeService eventTypeService;
	private final GuildApiService guildApiService;
	private final SquadApiAssembler squadApiAssembler;

	public Event fromDto(@NonNull EventApiDto dto) {
		final Guild ownerGuild = guildApiService.getTokenGuild();
		final Event event = Event.builder()
				.hidden(parseBoolean(dto.getHidden(), false))
				.shareable(parseBoolean(dto.getShareable(), false))
				.name(dto.getName().trim())
				.dateTime(dto.getDateTime())
				.creator(dto.getCreator().trim())
				.eventType(eventTypeService.find(dto.getEventType(), ownerGuild))
				.description(EventUtils.sanitize(dto.getDescription()))
				.missionType(dto.getMissionType())
				.missionLength(trim(dto.getMissionLength()))
				.pictureUrl(trim(dto.getPictureUrl()))
				.details(MinimalEventFieldAssembler.fromDtoIterable(dto.getDetails()))
				.squadList(squadApiAssembler.fromDtoList(dto.getSquadList()))
				.reserveParticipating(dto.getReserveParticipating())
				.ownerGuild(ownerGuild)
				.build();
		event.setBackReferences();
		return event;
	}

	public static EventApiIdDto toDto(@NonNull Event event) {
		return EventApiIdDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.creator(event.getCreator())
				.eventType(EventTypeApiAssembler.toDto(event.getEventType()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.pictureUrl(event.getPictureUrlWithDefault())
				.details(MinimalEventFieldIdAssembler.toDtoList(event.getDetails()))
				.squadList(SquadApiAssembler.toIdDtoList(event.getSquadList()))
				.reserveParticipating(event.getReserveParticipating())
				.build();
	}

	public static List<EventApiIdDto> toDtoList(@NonNull Iterable<? extends Event> events) {
		return StreamSupport.stream(events.spliterator(), false)
				.map(EventApiAssembler::toDto)
				.toList();
	}
}
