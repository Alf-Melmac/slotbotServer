package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.event.EventApiAssembler;
import de.webalf.slotbot.model.dtos.api.event.view.EventApiIdDto;
import de.webalf.slotbot.service.api.EventApiService;
import de.webalf.slotbot.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_WRITE_PERMISSION;

/**
 * @author Alf
 * @since 10.06.2023
 */
@RequestMapping(API + "/events/{id}/slot/{slotNumber}")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlottingApiController {
	private final EventApiService eventApiService;

	@PostMapping
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	public EventApiIdDto postSlot(@PathVariable(value = "id") long eventId, @PathVariable(value = "slotNumber") int slotNumber, @RequestBody(required = false) String userId) {
		if (StringUtils.isNotEmpty(userId)) {
			log.trace("postSlot: {} {} {}", eventId, slotNumber, userId);
			return EventApiAssembler.toDto(eventApiService.slot(eventId, slotNumber, userId));
		}

		log.trace("postUnslot: {} {}", eventId, slotNumber);
		return EventApiAssembler.toDto(eventApiService.unslot(eventId, slotNumber));
	}

	@PostMapping("/block")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	public EventApiIdDto postBlockSlot(@PathVariable(value = "id") long eventId, @PathVariable(value = "slotNumber") int slotNumber, @RequestBody(required = false) String replacementText) {
		log.trace("postBlockSlot: {} {} {}", eventId, slotNumber, replacementText);
		return EventApiAssembler.toDto(eventApiService.blockSlot(eventId, slotNumber, replacementText));
	}
}
