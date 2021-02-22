package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.controller.Urls.API;

/**
 * @author Alf
 * @since 22.02.2021
 */
@RequestMapping(API + "/events/view")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiViewDtoController {
	private final EventRepository eventRepository;
	private final EventApiAssembler eventApiAssembler;

	@GetMapping("/{id}")
	public EventApiViewDto getEventById(@PathVariable(value = "id") long eventId) {
		log.trace("getEventById: " + eventId);
		final Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		return eventApiAssembler.toViewDto(event);
	}
}
