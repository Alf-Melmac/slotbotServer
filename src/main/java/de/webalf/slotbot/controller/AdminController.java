package de.webalf.slotbot.controller;

import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.service.EventCalendarService;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.integration.GuildDiscordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.constant.Urls.ADMIN;
import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_ROLE_SYS_ADMIN;

/**
 * @author Alf
 * @since 11.06.2021
 */
@RestController
@RequestMapping(ADMIN + "/utils")
@PreAuthorize(HAS_ROLE_SYS_ADMIN)
@RequiredArgsConstructor
public class AdminController {
	private final FileService fileService;
	private final EventTypeService eventTypeService;
	private final EventNotificationService eventNotificationService;
	private final EventCalendarService eventCalendarService;
	private final GuildService guildService;
	private final GuildDiscordService guildDiscordService;

	@PostMapping("/{action}")
	public ResponseEntity<Void> postAction(@PathVariable String action, @RequestBody(required = false) String body) {
		switch (action) {
			case "deleteUnusedEventTypes" -> eventTypeService.deleteUnused();
			case "rebuildEventNotifications" -> eventNotificationService.rebuildAllNotifications();
			case "rebuildCalendars" -> guildService.findAll().forEach(eventCalendarService::rebuildCalendar);
			case "leaveGuild" -> guildDiscordService.leaveGuild(Long.parseLong(body));
			default -> {
				return ResponseEntity.badRequest().build();
			}
		}
		return ResponseEntity.ok().build();
	}
}
