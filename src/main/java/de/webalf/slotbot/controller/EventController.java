package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.assembler.website.CalendarEventAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
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
@Slf4j
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

	@PutMapping("/{id}")
	public EventDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventDto event) {
		event.setId(eventId);
		return EventAssembler.toDto(eventService.updateEvent(event));
	}

	@PostMapping("/editable")
	public EventDto updateEventEditable(long pk, String name, String value) {
		EventDto dto = EventDto.builder().id(pk).build();
		try {
			ReflectionUtils.setField(dto.getClass().getDeclaredField(name), dto, value);
		} catch (NoSuchFieldException e) {
			log.error("Can't find field " + name + " while trying to edit it.", e);
			throw BusinessRuntimeException.builder().title(name + " nicht gefunden").cause(e).build();
		}
		return EventAssembler.toDto(eventService.updateEvent(dto));
	}
}
