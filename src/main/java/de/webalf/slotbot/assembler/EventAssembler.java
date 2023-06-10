package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 23.06.2020
 */
@UtilityClass
public final class EventAssembler {
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
