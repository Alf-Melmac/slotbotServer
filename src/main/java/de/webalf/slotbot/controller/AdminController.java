package de.webalf.slotbot.controller;

import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.EventCalendarService;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/{action}")
	public ResponseEntity<Void> postAction(@PathVariable String action) {
		if ("listFiles".equals(action)) {
			fileService.listFiles();
		} else if ("deleteUnusedEventTypes".equals(action)) {
			eventTypeService.deleteUnused();
		} else if ("rebuildEventNotifications".equals(action)) {
			eventNotificationService.rebuildAllNotifications();
		} else if ("rebuildCalendars".equals(action)) {
			for (Guild guild : guildService.findAll()) {
				eventCalendarService.rebuildCalendar(guild);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
