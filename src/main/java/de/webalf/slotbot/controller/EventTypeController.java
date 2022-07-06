package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.service.EventTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events/types")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventTypeController {
	private final EventTypeService eventTypeService;

	@GetMapping
//	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public List<EventTypeDto> getEventTypes() {
		return EventTypeAssembler.toDtoList(eventTypeService.findAll());
	}
}
