package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.service.UserUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Wrapper for {@link UserService} to be used by discord bot
 *
 * @author Alf
 * @since 22.02.2021
 */
@Service
@RequiredArgsConstructor
public class UserBotService {
	private final UserService userService;
	private final UserUpdateService userUpdateService;

	public User findUser(long userId) {
		return userService.find(userId);
	}

	public void updateUser(UserDto userDto) {
		userUpdateService.update(userDto);
	}
}
