package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Wrapper for {@link UserService} to be used by discord bot
 *
 * @author Alf
 * @since 22.02.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserBotService {
	private final UserService userService;

	public User findUser(long userId) {
		return userService.find(userId);
	}

	public void updateUser(UserDto userDto) {
		userService.update(userDto);
	}
}
