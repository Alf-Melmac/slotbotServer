package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.controller.website.EventWebController;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.model.dtos.api.EventRecipientApiDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 04.11.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class EventApiAssembler {
	private final SquadApiAssembler squadApiAssembler;

	public static EventApiDto toDto(@NonNull Event event) {
		LocalDateTime dateTime = event.getDateTime();

		return EventApiDto.builder()
				.url(getUrl(event.getId()))
				.id(event.getId())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.hidden(event.isHidden())
				.channel(LongUtils.toString(event.getChannel()))
				.squadList(SquadApiAssembler.toDtoList(event.getSquadList()))
				.infoMsg(LongUtils.toString(event.getInfoMsg()))
				.slotListMsg(LongUtils.toString(event.getSlotListMsg()))
				.description(event.getDescription())
				.pictureUrl(event.getPictureUrl())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.details(EventFieldApiAssembler.toDtoList(event.getDetails()))
				.build();
	}

	/**
	 * To be used if a recipient must be defined
	 */
	public static EventRecipientApiDto toActionDto(Event event, User recipient) {
		EventRecipientApiDto eventRecipientApiDto = EventRecipientApiDto.builder().recipient(UserAssembler.toDto(recipient)).build();
		ReflectionUtils.shallowCopyFieldState(toDto(event), eventRecipientApiDto);
		return eventRecipientApiDto;
	}

	public EventApiViewDto toViewDto(Event event) {
		return EventApiViewDto.builder()
				.id(event.getId())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.squadList(squadApiAssembler.toViewDtoList(event.getSquadList()))
				.url(getUrl(event.getId()))
				.build();
	}

	private static String getUrl(long eventId) {
		return linkTo(methodOn(EventWebController.class).getEventDetailsHtml(eventId)).toUri().toString();
	}
}
