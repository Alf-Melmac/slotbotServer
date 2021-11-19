package de.webalf.slotbot.service;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alf
 * @since 19.11.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//This class and UserServiceImpl can be removed once guild is a database entity and knows it events. Then EventCalendarService doesn't need EventService and circular dependency is prevented
public class UserUpdateService {
	private final UserServiceImpl userService;
	private final EventCalendarService eventCalendarService;

	public User update(@NonNull UserDto userDto) {
		User user = userService.find(LongUtils.parseLong(userDto.getId()));

		DtoUtils.ifPresentParse(userDto.getSteamId64(), user::setSteamId64);
		DtoUtils.ifPresent(userDto.getExternalCalendarIntegrationActive(), externalCalendarIntegrationActive -> {
			user.setExternalCalendarIntegrationActive(externalCalendarIntegrationActive);
			eventCalendarService.rebuildCalendar(user);
		});

		return user;
	}
}
