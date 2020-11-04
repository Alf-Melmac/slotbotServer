package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.assembler.SquadAssembler;
import de.webalf.slotbot.controller.website.EventWebController;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.util.LongUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 04.11.2020
 */
@Component
public final class EventApiAssembler {
	public static EventApiDto toDto(Event event) {
		LocalDateTime dateTime = event.getDateTime();

		return EventApiDto.builder()
				.url(linkTo(methodOn(EventWebController.class).getEventDetailsHtml(event.getId())).toUri().toString())
				.id(event.getId())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.channel(LongUtils.toString(event.getChannel()))
				.squadList(SquadAssembler.toEventDtoList(event.getSquadList()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.pictureUrl(event.getPictureUrl())
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
}
