package de.webalf.slotbot.feature.action_log;

import de.webalf.slotbot.feature.action_log.dto.ActionLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alf
 * @since 07.03.2025
 */
@RestController
@RequestMapping("/action-logs")
@RequiredArgsConstructor
public class ActionLogController {
	private final ActionLogService actionLogService;
	private final ActionLogAssembler actionLogAssembler;

	@GetMapping("/events/{eventId}")
	@PreAuthorize("@permissionChecker.hasEventManagePermissionForEvent(#eventId)")
	public List<ActionLogDto> getEventLogs(@PathVariable(value = "eventId") long eventId) {
		return actionLogAssembler.toDtoList(actionLogService.getActionLogsByObjectId(eventId));
	}
}
