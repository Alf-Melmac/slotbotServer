package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.util.permissions.ApiPermissionChecker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_READ_PUBLIC_PERMISSION;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_WRITE_PERMISSION;

/**
 * @author Alf
 * @since 22.06.2020
 */
@RequestMapping(API + "/events")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiController {
	private final EventService eventService;
	private final ApiPermissionChecker apiPermissionChecker;

	@GetMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_READ_PUBLIC_PERMISSION)
	public EventApiDto getEvent(@PathVariable(value = "id") long eventId) {
		log.trace("getEvent: {}", eventId);
		return EventApiAssembler.toDto(eventService.findByIdForApi(eventId));
	}

	@PostMapping
	@PreAuthorize("@apiPermissionChecker.assertApiWriteAccess(#event)")
	public EventApiDto postEvent(@Valid @RequestBody EventDto event) {
		log.trace("postEvent: {}", event.getName());
		return EventApiAssembler.toDto(eventService.createEvent(event));
	}

	@PutMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	public EventApiDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventDto event) {
		log.trace("updateEvent: {}", event.getName());
		event.setId(eventId);
		apiPermissionChecker.assertApiWriteAccess(eventService.findById(eventId));
		return EventApiAssembler.toDto(eventService.updateEvent(event));
	}
}
