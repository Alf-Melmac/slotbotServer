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

	@GetMapping("/channel/{id}")
	public EventDto getEventByChannelId(@PathVariable(value = "id") long channel) {
		log.trace("getEventByChannelId: " + channel);
		Event event = eventService.findByChannel(channel);
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

	@PostMapping("/channel/{channelId}/slot/{slotNumber}")
	public EventDto postSlotInEventByChannelId(@PathVariable(value = "channelId") long channel,
	                                           @PathVariable(value = "slotNumber") int slotNumber,
	                                           @RequestBody long userId) {
		log.trace("postSlotInEventByChannelId: " + channel + " " + slotNumber + " " + userId);
		return EventAssembler.toDto(eventService.slot(channel, slotNumber, userId));
	}

	@PostMapping("/channel/{channelId}/unslot")
	public EventDto postUnslotInEventByChannelId(@PathVariable(value = "channelId") long channel,
	                                             @RequestBody long userId) {
		log.trace("postUnslotInEventByChannelId: " + channel + " " + userId);
		return EventAssembler.toDto(eventService.unslot(channel, userId));
	}
}
