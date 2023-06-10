package de.webalf.slotbot.service.api;

import de.webalf.slotbot.assembler.api.event.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.event.creation.EventApiDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.MentionUtils;
import de.webalf.slotbot.util.permissions.ApiPermissionChecker;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for {@link EventService} to be used by the api endpoints
 *
 * @author Alf
 * @since 08.06.2023
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiService {
	private final EventService eventService;
	private final EventApiAssembler eventApiAssembler;
	private final MessageSource messageSource;

	/**
	 * In addition to {@link EventService#findById(long)}, the API access rights for the event are checked
	 */
	public Event findById(long eventId) {
		final Event event = eventService.findById(eventId);
		EventUtils.assertApiReadAccess(event);
		return event;
	}

	/**
	 * Creates a new event with values from the {@link EventApiDto}
	 *
	 * @param dto new event
	 * @return saved new event
	 */
	public Event create(@NonNull EventApiDto dto) {
		return eventService.save(eventApiAssembler.fromDto(dto));
	}

	public void delete(long eventId) {
		eventService.deleteEvent(eventService.findById(eventId));
	}

	public Event slot(long eventId, int slotNumber, String userId) {
		final Event event = eventService.findById(eventId);
		ApiPermissionChecker.assertApiWriteAccess(event);

		if (!MentionUtils.isSnowflake(userId)) {
			throw BusinessRuntimeException.builder()
					.title(messageSource.getMessage("api.userId.error", null, event.getOwnerGuildLocale()))
					.build();
		}
		return eventService.slot(event, slotNumber, Long.parseLong(userId));
	}

	public Event blockSlot(long eventId, int slotNumber, String replacementText) {
		final Event event = eventService.findById(eventId);
		ApiPermissionChecker.assertApiWriteAccess(event);

		return eventService.blockSlot(event, slotNumber, replacementText);
	}

	public Event unslot(long eventId, int slotNumber) {
		final Event event = eventService.findById(eventId);
		ApiPermissionChecker.assertApiWriteAccess(event);

		return eventService.unslot(event, slotNumber);
	}
}
