package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.service.bot.EventNotificationService;
import de.webalf.slotbot.service.bot.EventNotificationService.NotificationIdentifier;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.webalf.slotbot.configuration.springdoc.TagNames.STATUS;
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
@Tag(name = STATUS, description = "Server Status")
public class StatusApiController {
	@GetMapping
	@Operation(summary = "Ping", description = "Check if the server is responsive.")
	public ResponseEntity<Void> ping() {
		log.trace("ping");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/scheduledNotifications")
	@Hidden
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public Map<NotificationIdentifier, Long> getAllScheduledEventNotifications() {
		return EventNotificationService.getSCHEDULED_NOTIFICATIONS()
				.entrySet()
				.stream()
				.map(entry -> Map.entry(entry.getKey(), entry.getValue().getDelay(TimeUnit.MINUTES)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
