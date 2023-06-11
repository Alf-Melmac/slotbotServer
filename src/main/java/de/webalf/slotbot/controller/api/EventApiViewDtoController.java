package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.EventApiViewAssembler;
import de.webalf.slotbot.configuration.springdoc.TagNames;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.service.api.EventApiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.configuration.springdoc.OpenApiConfig.SECURITY_KEY_READ_PUBLIC;
import static de.webalf.slotbot.constant.Urls.UNSTABLE;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_READ_PUBLIC_PERMISSION;

/**
 * @author Alf
 * @since 22.02.2021
 */
@RequestMapping(UNSTABLE + "/events/view")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = TagNames.UNSTABLE, description = "Unstable API")
@SecurityRequirement(name = SECURITY_KEY_READ_PUBLIC)
public class EventApiViewDtoController {
	private final EventApiService eventService;
	private final EventApiViewAssembler eventApiAssembler;

	@GetMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_READ_PUBLIC_PERMISSION)
	public EventApiViewDto getEventView(@PathVariable(value = "id") long eventId) {
		log.trace("getEventView: {}", eventId);
		return eventApiAssembler.toViewDto(eventService.findById(eventId));
	}
}
