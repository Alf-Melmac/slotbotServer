package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.service.bot.EventNotificationService;
import de.webalf.slotbot.service.bot.EventNotificationService.NotificationIdentifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;

/**
 * @author Alf
 * @since 14.08.2020
 */
@RequestMapping(API + "/status")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatusApiController {
	@GetMapping
	public ResponseEntity<Void> ping() {
		log.trace("ping");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/scheduledNotifications")
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public Map<NotificationIdentifier, ScheduledFuture<?>> getAllScheduledEventNotifications() {
		return EventNotificationService.getSCHEDULED_NOTIFICATIONS();
	}
}
