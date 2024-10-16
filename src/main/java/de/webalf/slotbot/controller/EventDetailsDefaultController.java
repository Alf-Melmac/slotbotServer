package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.event.EventDetailsDefaultAssembler;
import de.webalf.slotbot.model.dtos.EventDetailDefaultDto;
import de.webalf.slotbot.service.EventDetailsDefaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 03.08.2024
 */
@RestController
@RequestMapping("/events/details/defaults")
@RequiredArgsConstructor
public class EventDetailsDefaultController {
	private final EventDetailsDefaultService eventDetailsDefaultService;

	@GetMapping({"", "/guild/{guild}"})
	@PreAuthorize("@permissionChecker.hasEventManagePermissionIn(#guild)")
	public List<EventDetailDefaultDto> getEventFieldDefaults(@PathVariable(required = false) Optional<String> guild,
	                                                         @RequestParam String eventTypeName) {
		return EventDetailsDefaultAssembler.toDto(eventDetailsDefaultService.findByName(eventTypeName, guild));
	}

	@GetMapping("/{guildId}")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#guildId)")
	public List<EventDetailDefaultDto> getEventFieldDefaults(@PathVariable(value = "guildId") long guildId,
	                                                         @RequestParam String eventTypeName) {
		return EventDetailsDefaultAssembler.toDto(eventDetailsDefaultService.findByName(eventTypeName, guildId));
	}

	@PutMapping("/{guildId}")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public List<EventDetailDefaultDto> putEventFieldDefaults(@PathVariable(value = "guildId") long guildId,
	                                                         @RequestParam String eventTypeName,
	                                                         @RequestBody List<EventDetailDefaultDto> eventDetails) {
		return EventDetailsDefaultAssembler.toDto(eventDetailsDefaultService.updateDefaults(eventTypeName, eventDetails, guildId));
	}
}
