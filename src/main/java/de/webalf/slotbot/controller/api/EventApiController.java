package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static de.webalf.slotbot.controller.Urls.API;

/**
 * @author Alf
 * @since 22.06.2020
 */
@RequestMapping(API + "/events")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiController {
	private final EventService eventService;

	@PostMapping("")
	public EventDto postEvent(@Valid @RequestBody EventDto event) {
		log.trace("postEvent: " + event.getName());
		return EventAssembler.toDto(eventService.createEvent(event));
	}

	@PutMapping("/{id}")
	public EventDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventDto event) {
		log.trace("updateEvent: " + event.getName());
		event.setId(eventId);
		return EventAssembler.toDto(eventService.updateEvent(event));
	}
}
