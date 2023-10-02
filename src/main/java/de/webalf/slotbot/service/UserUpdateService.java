package de.webalf.slotbot.service;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.webalf.slotbot.util.permissions.PermissionHelper.getLoggedInUserId;

/**
 * @author Alf
 * @since 19.11.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserUpdateService {
	private final UserServiceImpl userService;
	private final EventCalendarService eventCalendarService;

	public User updateSteamId(String steamId) {
		User user = userService.find(Long.parseLong(getLoggedInUserId()));

		user.setSteamId64(LongUtils.parseLongWrapper(steamId));

		return user;
	}

	public User updateSettings(boolean externalCalendarIntegrationActive) {
		User user = userService.find(Long.parseLong(getLoggedInUserId()));

		user.setExternalCalendarIntegrationActive(externalCalendarIntegrationActive);
		eventCalendarService.rebuildCalendar(user);

		return user;
	}

	public User find(long id) {
		return userService.find(id);
	}

	public User find(String id) {
		return find(Long.parseLong(id));
	}
}
