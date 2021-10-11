package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.GuildUtils.GUILD_PLACEHOLDER;

/**
 * @author Alf
 * @since 23.06.2020
 */
@UtilityClass
public final class EventAssembler {

	public static Event fromDto(EventDto dto) {
		if (dto == null) {
			return null;
		}

		return Event.builder()
				.id(dto.getId())
				.hidden(dto.isHidden())
				.shareable(dto.isShareable())
				.name(dto.getName().trim())
				.dateTime(LocalDateTime.of(dto.getDate(), dto.getStartTime()))
				.creator(dto.getCreator().trim())
				//Event type is set by using service
				.description(dto.getDescription())
				.missionType(dto.getMissionType())
				.missionLength(dto.getMissionLength())
				.pictureUrl(dto.getRawPictureUrl())
				.details(EventFieldAssembler.fromDtoIterable(dto.getDetails()))
				.squadList(SquadAssembler.fromDtoList(dto.getSquadList()))
				.reserveParticipating(dto.getReserveParticipating())
				.discordInformation(EventDiscordInformationAssembler.fromDtoIterable(dto.getDiscordInformation()))
				.ownerGuild(LongUtils.parseLong(dto.getOwnerGuild(), GUILD_PLACEHOLDER))
				.build();
	}

	/**
	 * To be used if the focus relies on the event. Does include squadList and details
	 */
	public static EventReferencelessDto toReferencelessDto(Event event) {
		final LocalDateTime dateTime = event.getDateTime();
		return EventReferencelessDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.shareable(event.isShareable())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
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
				.ownerGuild(Long.toString(event.getOwnerGuild()))
				.build();
	}

	/**
	 * To be used if the focus relies not on the event. Doesn't include the squadlist or details
	 */
	static EventDto toAbstractDto(Event event) {
		final LocalDateTime dateTime = event.getDateTime();
		return EventDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.shareable(event.isShareable())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.pictureUrl(event.getPictureUrl())
				.reserveParticipating(event.getReserveParticipating())
				.discordInformation(EventDiscordInformationAssembler.toDtoSet(event.getDiscordInformation()))
				.ownerGuild(Long.toString(event.getOwnerGuild()))
				.build();
	}
}
