package de.webalf.slotbot.service;

import de.webalf.slotbot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.webalf.slotbot.util.permissions.PermissionHelper.getLoggedInUserIdLong;

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

	public Long updateSteamId(Long steamId) {
		final User user = find(getLoggedInUserIdLong());

		user.setSteamId64(steamId);

		return steamId;
	}

	public User updateSettings(boolean externalCalendarIntegrationActive) {
		final User user = find(getLoggedInUserIdLong());

		user.setExternalCalendarIntegrationActive(externalCalendarIntegrationActive);
		eventCalendarService.rebuildCalendar(user);

		return user;
	}

	public User find(long id) {
		return userService.find(id);
	}
}
