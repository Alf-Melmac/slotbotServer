package de.webalf.slotbot.feature.notifications;

import de.webalf.slotbot.feature.notifications.dto.NotificationSettingDto;
import de.webalf.slotbot.feature.notifications.dto.NotificationSettingPutDto;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_ROLE_EVERYONE;

/**
 * @author Alf
 * @since 12.08.2021
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationSettingsController {
	private final NotificationSettingsService notificationSettingsService;
	private final UserService userService;

	@PutMapping("/own")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public List<NotificationSettingDto> updateNotificationSettings(@RequestBody List<NotificationSettingPutDto> notificationSettings) {
		return NotificationSettingAssembler.toReferencelessDtoList(
				notificationSettingsService.updateGlobalNotificationSettings(userService.getLoggedIn(), notificationSettings)
		);
	}
}
