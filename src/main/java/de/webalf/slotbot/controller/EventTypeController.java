package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.service.EventTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events/types")
@RequiredArgsConstructor
public class EventTypeController {
	private final EventTypeService eventTypeService;

	@GetMapping({"", "/guild/{guild}"})
	@PreAuthorize("@permissionChecker.hasEventManagePermissionIn(#guild)")
	public List<EventTypeDto> getEventTypes(@PathVariable(required = false) Optional<String> guild) {
		return EventTypeAssembler.toDtoList(eventTypeService.findAllOrdered(guild));
	}

	@GetMapping("/{guildId}")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#guildId)")
	public List<EventTypeDto> getEventTypes(@PathVariable(value = "guildId") long guildId) {
		return EventTypeAssembler.toDtoList(eventTypeService.findAllOrdered(guildId));
	}
}
