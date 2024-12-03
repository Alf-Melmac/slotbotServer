package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailsDefault;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.constant.Urls.UNSTABLE;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;

/**
 * @author Alf
 * @since 02.12.2024
 */
@RequestMapping(UNSTABLE + "/events/types/defaults/migration")
@RestController
@RequiredArgsConstructor
@Slf4j
public class EventDetailDefaultMigrationApiController {
	private final EventDetailDefaultRepository eventDetailDefaultRepository;
	private final EventDetailsDefaultRepository eventDetailsDefaultRepository;
	private final EventTypeRepository eventTypeRepository;
	private final EventRepository eventRepository;

	@GetMapping
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public void migrate() {
		//For every global event type
		//check which guild has an event with that type
		//create a new event type for that guild
		final List<EventType> globalEventTypes = eventTypeRepository.findByGuildNull();
		log.info("Found {} global event types", globalEventTypes.size());
		globalEventTypes.forEach(get -> {
			final List<EventInfo> distinctByEventType = eventRepository.findDistinctByEventType(get);
			distinctByEventType.forEach(eventInfo -> {
				log.info("Creating event type {} for guild {}", eventInfo.eventType().getName(), eventInfo.ownerGuild().getGroupIdentifier());
				eventTypeRepository.save(EventType.builder()
						.name(eventInfo.eventType().getName())
						.color(eventInfo.eventType().getColor())
						.guild(eventInfo.ownerGuild())
						.build());
			});
		});

		//For every event referencing a global event type replace it with the guild event type
		eventRepository.findByEventTypeIn(globalEventTypes).forEach(event -> {
			log.info("Migrating event {} with type {} of guild {} to new event type", event.getId(), event.getEventType().getName(), event.getOwnerGuild().getGroupIdentifier());
			final EventType eventType = eventTypeRepository.findByNameAndGuild(event.getEventType().getName(), event.getOwnerGuild())
					.orElseThrow(() -> new ResourceNotFoundException("eventType1"));
			event.setEventType(eventType);
		});

		eventDetailDefaultRepository.findAll().forEach(eventDetailDefault -> {
			log.info("Migrating event detail default {} - {}", eventDetailDefault.getId(), eventDetailDefault.getTitle());
			final long detailsId = eventDetailDefault.getEventDetailsDefault();
			final EventDetailsDefault details = eventDetailsDefaultRepository.findById(detailsId)
					.orElseThrow(() -> new ResourceNotFoundException("details"));
			final EventType eventType = eventTypeRepository.findByNameAndGuild(details.getEventTypeName(), details.getGuild())
					.orElseThrow(() -> new ResourceNotFoundException("eventType"));
			eventDetailDefault.setEventType(eventType);
			eventDetailDefault.setEventDetailsDefault(null);
		});
	}
}
