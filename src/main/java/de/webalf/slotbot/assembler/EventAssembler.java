package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

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
				.name(dto.getName().trim())
				.dateTime(LocalDateTime.of(dto.getDate(), dto.getStartTime()))
				.creator(dto.getCreator().trim())
				.hidden(dto.isHidden())
				.channel(LongUtils.parseLongWrapper(dto.getChannel()))
				.squadList(SquadAssembler.fromDtoList(dto.getSquadList()))
				.infoMsg(LongUtils.parseLongWrapper(dto.getInfoMsg()))
				.slotListMsg(LongUtils.parseLongWrapper(dto.getSlotListMsg()))
				.description(dto.getDescription())
				.pictureUrl(dto.getPictureUrl())
				.missionType(dto.getMissionType())
				.missionLength(dto.getMissionLength())
				.reserveParticipating(dto.getReserveParticipating())
				.details(EventFieldAssembler.fromDtoIterable(dto.getDetails()))
				.build();
	}

	/**
	 * To be used if the focus relies on the event
	 */
	public static EventReferencelessDto toReferencelessDto(Event event) {
		final LocalDateTime dateTime = event.getDateTime();
		return EventReferencelessDto.builder()
				.id(event.getId())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.hidden(event.isHidden())
				.channel(LongUtils.toString(event.getChannel()))
				.squadList(SquadAssembler.toReferencelessDtoList(event.getSquadList()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.pictureUrl(event.getPictureUrl())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.details(EventFieldAssembler.toReferencelessDtoList(event.getDetails()))
				.build();
	}

	/**
	 * To be used if the focus relies not on the event. It doesn't include the squadlist or details
	 */
	static EventDto toAbstractDto(Event event) {
		final LocalDateTime dateTime = event.getDateTime();
		return EventDto.builder()
				.id(event.getId())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.hidden(event.isHidden())
				.channel(LongUtils.toString(event.getChannel()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.pictureUrl(event.getPictureUrl())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.build();
	}
}
