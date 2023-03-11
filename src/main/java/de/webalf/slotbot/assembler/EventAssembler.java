package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.BooleanUtils.parseBoolean;
import static de.webalf.slotbot.util.StringUtils.trim;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventAssembler {
	private final EventDiscordInformationAssembler discordInformationAssembler;
	private final GuildService guildService;

	public Event fromDto(EventDto dto) {
		if (dto == null) {
			return null;
		}

		final Event event = Event.builder()
				.id(dto.getId())
				.hidden(parseBoolean(dto.getHidden(), false))
				.shareable(parseBoolean(dto.getShareable(), false))
				.name(dto.getName().trim())
				.dateTime(dto.getDateTime())
				.creator(dto.getCreator().trim())
				//Event type is set by using service
				.description(trim(dto.getDescription()))
				.missionType(dto.getMissionType())
				.missionLength(trim(dto.getMissionLength()))
				.pictureUrl(trim(dto.getRawPictureUrl()))
				.details(EventFieldAssembler.fromDtoIterable(dto.getDetails()))
				.squadList(SquadAssembler.fromDtoList(dto.getSquadList()))
				.reserveParticipating(dto.getReserveParticipating())
				.discordInformation(discordInformationAssembler.fromDtoIterable(dto.getDiscordInformation()))
				.ownerGuild(guildService.find(LongUtils.parseLong(dto.getOwnerGuild(), GUILD_PLACEHOLDER)))
				.build();
		event.setBackReferences();
		return event;
	}

	/**
	 * To be used if the focus relies on the event. Does include squadList and details
	 */
	public static EventReferencelessDto toReferencelessDto(Event event) {
		return EventReferencelessDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.shareable(event.isShareable())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.creator(event.getCreator())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.pictureUrl(event.getPictureUrl())
				.details(EventFieldAssembler.toReferencelessDtoList(event.getDetails()))
				.squadList(SquadAssembler.toReferencelessDtoList(event.getSquadList()))
				.reserveParticipating(event.getReserveParticipating())
				.discordInformation(EventDiscordInformationAssembler.toDtoSet(event.getDiscordInformation()))
				.ownerGuild(Long.toString(event.getOwnerGuild().getId()))
				.build();
	}

	/**
	 * To be used if the focus relies not on the event. Doesn't include the squadlist or details
	 */
	static EventDto toAbstractDto(Event event) {
		return EventDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.shareable(event.isShareable())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.creator(event.getCreator())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.pictureUrl(event.getPictureUrl())
				.reserveParticipating(event.getReserveParticipating())
				.discordInformation(EventDiscordInformationAssembler.toDtoSet(event.getDiscordInformation()))
				.ownerGuild(Long.toString(event.getOwnerGuild().getId()))
				.build();
	}
}
