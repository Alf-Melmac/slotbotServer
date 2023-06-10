package de.webalf.slotbot.assembler.website.event.creation;

import de.webalf.slotbot.assembler.minimal.MinimalEventFieldAssembler;
import de.webalf.slotbot.assembler.website.event.EventActionAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.event.creation.EventPostDto;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.GuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.webalf.slotbot.util.BooleanUtils.parseBoolean;
import static de.webalf.slotbot.util.StringUtils.trimAndNullify;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventPostAssembler {
	private final EventTypeService eventTypeService;
	private final MinimalSquadAssembler squadAssembler;
	private final GuildService guildService;

	public Event fromDto(@NonNull EventPostDto dto) {
		final Guild ownerGuild = guildService.findCurrentNonNullGuild();
		final Event event = Event.builder()
				.hidden(parseBoolean(dto.getHidden(), false))
				.shareable(parseBoolean(dto.getShareable(), false))
				.name(trimAndNullify(dto.getName()))
				.dateTime(dto.getDateTime())
				.creator(trimAndNullify(dto.getCreator()))
				.eventType(eventTypeService.find(dto.getEventType(), ownerGuild))
				.description(trimAndNullify(dto.getDescription()))
				.missionType(trimAndNullify(dto.getMissionType()))
				.missionLength(trimAndNullify(dto.getMissionLength()))
				.pictureUrl(trimAndNullify(dto.getPictureUrl()))
				.details(MinimalEventFieldAssembler.fromDtoIterable(dto.getDetails()))
				.squadList(squadAssembler.fromDtoList(dto.getSquadList()))
				.reserveParticipating(dto.getReserveParticipating())
				.ownerGuild(ownerGuild)
				.build();
		event.setBackReferences();
		return event;
	}

	public static EventPostDto toDto(Event event) {
		final EventPostDto.EventPostDtoBuilder<?, ?> builder = EventPostDto.builder();
		EventActionAssembler.toDto(builder, event);
		return builder
				.details(MinimalEventFieldAssembler.toDtoList(event.getDetails()))
				.squadList(MinimalSquadAssembler.toDtoList(event.getSquadList()))
				.build();
	}
}
