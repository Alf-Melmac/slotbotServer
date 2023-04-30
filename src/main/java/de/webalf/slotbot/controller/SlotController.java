package de.webalf.slotbot.controller;

import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;

/**
 * @author Alf
 * @since 30.04.2023
 */
@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotController {
	private final EventService eventService;

	@PutMapping("/{id}/slotting")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public void putSlotting(@PathVariable(value = "id") long slotId) {
		eventService.slot(slotId);
	}
}
