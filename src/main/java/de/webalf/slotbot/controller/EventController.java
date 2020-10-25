package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.CalendarEventAssembler;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	private final EventRepository eventRepository;

	@GetMapping(value = "/list")
	public List<CalendarEventDto> getBetween(@RequestParam LocalDateTime start,
	                                         @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventRepository.findAllByDateTimeBetween(start, end));
	}
}
