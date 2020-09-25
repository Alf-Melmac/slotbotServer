package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.EventRecipientDto;
import de.webalf.slotbot.util.LongUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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
				.channel(LongUtils.parseLongWrapper(eventDto.getChannel()))
				.squadList(SquadAssembler.fromDtoList(eventDto.getSquadList()))
				.infoMsg(LongUtils.parseLongWrapper(eventDto.getInfoMsg()))
				.slotListMsg(LongUtils.parseLongWrapper(eventDto.getSlotListMsg()))
				.description(eventDto.getDescription())
				.missionType(eventDto.getMissionType())
				.missionLength(eventDto.getMissionLength())
				.reserveParticipating(eventDto.getReserveParticipating())
				.modPack(eventDto.getModPack())
				.map(eventDto.getMap())
				.missionTime(eventDto.getMissionTime())
				.navigation(eventDto.getNavigation())
				.technicalTeleport(eventDto.getTechnicalTeleport())
				.medicalSystem(eventDto.getMedicalSystem())
				.build();
	}

	/**
	 * To be used if the focus relies on the event
	 */
	public static EventDto toDto(Event event) {
		LocalDateTime dateTime = event.getDateTime();
		return EventDto.builder()
				.id(event.getId())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.channel(LongUtils.toString(event.getChannel()))
				.squadList(SquadAssembler.toEventDtoList(event.getSquadList()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.modPack(event.getModPack())
				.map(event.getMap())
				.missionTime(event.getMissionTime())
				.navigation(event.getNavigation())
				.technicalTeleport(event.getTechnicalTeleport())
				.medicalSystem(event.getMedicalSystem())
				.build();
	}

	/**
	 * To be used if the focus relies on a slot
	 */
	static EventDto toSlotDto(Event event) {
		LocalDateTime dateTime = event.getDateTime();
		return EventDto.builder()
				.id(event.getId())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.channel(LongUtils.toString(event.getChannel()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.modPack(event.getModPack())
				.map(event.getMap())
				.missionTime(event.getMissionTime())
				.navigation(event.getNavigation())
				.technicalTeleport(event.getTechnicalTeleport())
				.medicalSystem(event.getMedicalSystem())
				.build();
	}

	/**
	 * To be used if a recipient must be defined
	 */
	public static EventRecipientDto toActionDto(Event event, User recipient) {
		EventRecipientDto eventRecipientDto = EventRecipientDto.recipientBuilder().recipient(UserAssembler.toDto(recipient)).build();
		ReflectionUtils.shallowCopyFieldState(toDto(event), eventRecipientDto);
		return eventRecipientDto;
	}

	private static List<EventDto> toDtoList(List<Event> content) {
		return content.stream().map(EventAssembler::toDto).collect(Collectors.toList());
	}

	public static Page<EventDto> toDtoPage(Page<Event> eventPage, Pageable pageable) {
		List<EventDto> eventDtoList = toDtoList(eventPage.getContent());
		return new PageImpl<>(eventDtoList, pageable, eventPage.getTotalElements());
	}
}
