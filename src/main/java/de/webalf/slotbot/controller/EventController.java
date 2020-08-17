package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Alf
 * @since 22.06.2020
 */
@RequestMapping("/events")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController {
	private final EventRepository eventRepository;
	private final EventService eventService;

	@GetMapping("")
	public Page<EventDto> getEvents(Pageable pageable) {
		log.trace("getEvents");
		Page<Event> eventPage = eventRepository.findAll(pageable);
		return EventAssembler.toDtoPage(eventPage, pageable);
	}

	@GetMapping("/{id}")
	public EventDto getEventById(@PathVariable(value = "id") long eventId) {
		log.trace("getEventById: " + eventId);
		Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		return EventAssembler.toDto(event);
	}

	@PostMapping("")
	public EventDto postEvent(@Valid @RequestBody EventDto event) {
		log.trace("postEvent: " + event.getName());
		return EventAssembler.toDto(eventService.createEvent(event));
	}

	@PutMapping("/{id}/msgIds")
	public Event updateChannelIds(@PathVariable(value = "id") long eventId, @RequestBody EventDto event) {
		log.trace("updateChannelIds: " + event.getName());
		return eventService.updateEvent(eventId, event);
	}
}
