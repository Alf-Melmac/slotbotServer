package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.event.EventDetailsDefaultAssembler;
import de.webalf.slotbot.model.dtos.EventDetailDefaultDto;
import de.webalf.slotbot.service.EventDetailsDefaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_POTENTIALLY_ROLE_ADMIN;
import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;

/**
 * @author Alf
 * @since 03.08.2024
 */
@RestController
@RequestMapping("/events/details/defaults")
@RequiredArgsConstructor
public class EventDetailsDefaultController {
	private final EventDetailsDefaultService eventDetailsDefaultService;

	@GetMapping
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public List<EventDetailDefaultDto> getEventFieldDefaults(@RequestParam String eventTypeName) {
		return EventDetailsDefaultAssembler.toDto(eventDetailsDefaultService.findByName(eventTypeName));
	}

	@PutMapping
	@PreAuthorize(HAS_POTENTIALLY_ROLE_ADMIN)
	public List<EventDetailDefaultDto> putEventFieldDefaults(@RequestParam String eventTypeName, @RequestBody List<EventDetailDefaultDto> eventDetails) {
		return EventDetailsDefaultAssembler.toDto(eventDetailsDefaultService.updateDefaults(eventTypeName, eventDetails));
	}
}
