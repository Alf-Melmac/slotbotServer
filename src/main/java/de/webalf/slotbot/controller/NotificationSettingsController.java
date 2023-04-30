package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.NotificationSettingAssembler;
import de.webalf.slotbot.model.dtos.NotificationSettingDto;
import de.webalf.slotbot.model.dtos.referenceless.NotificationSettingsReferencelessDto;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;

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

	@PutMapping("/own")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public List<NotificationSettingsReferencelessDto> updateNotificationSettings(@RequestBody List<NotificationSettingDto> notificationSettings) {
		return NotificationSettingAssembler.toReferencelessDtoList(
				notificationSettingsService.updateGlobalNotificationSettings(userService.getLoggedIn(), notificationSettings)
		);
	}
}
