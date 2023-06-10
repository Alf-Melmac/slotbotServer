package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.event.EventApiAssembler;
import de.webalf.slotbot.model.dtos.api.event.creation.EventApiDto;
import de.webalf.slotbot.model.dtos.api.event.view.EventApiIdDto;
import de.webalf.slotbot.service.api.EventApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_READ_PUBLIC_PERMISSION;

/**
 * @author Alf
 * @since 22.06.2020
 */
@RequestMapping(API + "/events")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiController {
	private final EventApiService eventApiService;

	@GetMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_READ_PUBLIC_PERMISSION)
	public EventApiIdDto getEvent(@PathVariable(value = "id") long eventId) {
		log.trace("getEvent: {}", eventId);
		return EventApiAssembler.toDto(eventApiService.findById(eventId));
	}

	@PostMapping
	@PreAuthorize("@apiPermissionChecker.assertApiWriteAccess()")
	public EventApiIdDto postEvent(@Valid @RequestBody EventApiDto event) {
		log.trace("postEvent: {}", event.getName());
		return EventApiAssembler.toDto(eventApiService.create(event));
	}

	/*@PutMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	public EventApiIdDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventApiDto event) {
		log.trace("updateEvent: {}", event.getName());
		return EventApiAssembler.toDto(eventApiService.update(eventId, event));
	}*/

	@DeleteMapping("/{id}")
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public void deleteEvent(@PathVariable(value = "id") long eventId) {
		log.trace("deleteEvent: {}", eventId);
		eventApiService.delete(eventId);
	}
}
