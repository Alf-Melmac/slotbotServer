package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.CalendarEventAssembler;
import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alf
 * @since 25.10.2020
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController {
	private final EventService eventService;

	@GetMapping(value = "/list")
	public List<CalendarEventDto> getBetween(@RequestParam LocalDateTime start,
	                                         @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventService.findAllBetween(start, end));
	}

	@PostMapping
	public EventDto postEvent(@Valid @RequestBody EventDto event) {
		return EventAssembler.toDto(eventService.createEvent(event));
	}
}
