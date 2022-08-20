package de.webalf.slotbot.assembler.website.event;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
public final class EventActionAssembler {
	public static <C extends EventActionDto, B extends EventActionDto.EventActionDtoBuilder<C, B>> EventActionDto.EventActionDtoBuilder<C, B>
	toDto(EventActionDto.EventActionDtoBuilder<C, B> builder, @NonNull Event event) {
		final LocalDateTime dateTime = event.getDateTime();
		return builder
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
				.reserveParticipating(event.getReserveParticipating());
	}
}
