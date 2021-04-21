package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_READ_PUBLIC_PERMISSION;

/**
 * @author Alf
 * @since 22.02.2021
 */
@RequestMapping(API + "/events/view")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventApiViewDtoController {
	private final EventService eventService;
	private final EventApiAssembler eventApiAssembler;

	@GetMapping("/{id}")
	@PreAuthorize(HAS_READ_PUBLIC_PERMISSION)
	public EventApiViewDto getEventView(@PathVariable(value = "id") long eventId) {
		log.trace("getEventView: " + eventId);
		return eventApiAssembler.toViewDto(eventService.findByIdForApi(eventId));
	}
}
