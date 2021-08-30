package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.NotificationSettingAssembler;
import de.webalf.slotbot.model.dtos.NotificationSettingDto;
import de.webalf.slotbot.model.dtos.referenceless.NotificationSettingsReferencelessDto;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.assertIsLoggedInUser;

/**
 * @author Alf
 * @since 12.08.2021
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationSettingsController {
	private final NotificationSettingsService notificationSettingsService;
	private final UserService userService;

	@DeleteMapping("/{userId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> deleteAllByUser(@PathVariable(name = "userId") String userId) {
		assertIsLoggedInUser(userId);
		notificationSettingsService.deleteAllByUser(userService.find(Long.parseLong(userId)));
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{userId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public List<NotificationSettingsReferencelessDto> updateNotificationSettings(@PathVariable(name = "userId") String userId, @RequestBody List<NotificationSettingDto> notificationSettings) {
		assertIsLoggedInUser(userId);
		return NotificationSettingAssembler.toReferencelessDtoList(
				notificationSettingsService.updateGlobalNotificationSettings(userService.find(Long.parseLong(userId)), notificationSettings)
		);
	}
}
