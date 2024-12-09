package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.feature.event_type_defaults.dto.EventDetailDefaultDto;
import de.webalf.slotbot.feature.event_type_defaults.dto.EventDetailDefaultPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Alf
 * @since 03.08.2024
 */
@RestController
@RequestMapping("/events/types")
@RequiredArgsConstructor
public class EventTypeDefaultsController {
	private final EventDetailsDefaultService eventDetailsDefaultService;

	@GetMapping("/{guildId}/{eventTypeId}/defaults")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#guildId)")
	public List<EventDetailDefaultDto> getEventTypeDefaults(@PathVariable(value = "guildId") long guildId,
	                                                        @PathVariable(value = "eventTypeId") long eventTypeId) {
		return EventDetailDefaultAssembler.toDtoList(eventDetailsDefaultService.findByEventTypeId(guildId, eventTypeId));
	}

	@GetMapping("/guild/{guild}/{eventTypeId}/defaults")
	@PreAuthorize("@permissionChecker.hasEventManagePermissionIn(#guild)")
	public List<EventDetailDefaultDto> getEventTypeDefaults(@PathVariable(value = "guild") String guild,
	                                                        @PathVariable(value = "eventTypeId") long eventTypeId) {
		return EventDetailDefaultAssembler.toDtoList(eventDetailsDefaultService.findByEventTypeId(guild, eventTypeId));
	}

	@PutMapping("/{guildId}/{eventTypeId}/defaults")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public List<EventDetailDefaultDto> putEventTypeDefaults(@PathVariable(value = "guildId") long guildId,
	                                                         @PathVariable(value = "eventTypeId") long eventTypeId,
	                                                         @RequestBody List<EventDetailDefaultPostDto> eventDetails) {
		return EventDetailDefaultAssembler.toDtoList(eventDetailsDefaultService.updateDefaults(guildId, eventTypeId, eventDetails));
	}
}
