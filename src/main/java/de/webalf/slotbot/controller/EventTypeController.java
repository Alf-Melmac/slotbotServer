package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.service.EventTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;

@RestController
@RequestMapping("/events/types")
@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventTypeController {
	private final EventTypeService eventTypeService;

	@GetMapping
	public List<EventTypeDto> getEventTypes() {
		return EventTypeAssembler.toDtoList(eventTypeService.findAll());
	}
}
