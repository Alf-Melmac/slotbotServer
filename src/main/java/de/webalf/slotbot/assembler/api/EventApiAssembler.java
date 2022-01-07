package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.model.dtos.api.EventRecipientApiDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.EventUtils.buildUrl;

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
				.url(buildUrl(event.getId()))
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
				.details(EventFieldApiAssembler.toDtoList(event.getDetails()))
				.squadList(SquadApiAssembler.toDtoList(event.getSquadList()))
				.reserveParticipating(event.getReserveParticipating())
				.ownerGuild(Long.toString(event.getOwnerGuild().getId()))
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
				.url(buildUrl(event.getId()))
				.build();
	}
}
