package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.UserNameDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {
	private final UserRepository userRepository;
	private final DiscordApiService discordApiService;
	private final UserServiceImpl userServiceImpl;

	User find(@NonNull UserDto userDto) {
		return userRepository.findById(LongUtils.parseLong(userDto.getId()))
				.orElseGet(() -> userServiceImpl.createUser(userDto));
	}

	public User find(long id) {
		return userServiceImpl.find(id);
	}

	public UserNameDto toUserNameDto(User user) {
		if (user == null || user.isDefaultUser()) {
			return null;
		}

		final String userId = LongUtils.toString(user.getId());
		return toUserNameDto(user, discordApiService.getName(userId));
	}

	public UserNameDto toUserNameDto(User user, @NonNull Guild guild) {
		if (user == null || user.isDefaultUser()) {
			return null;
		}

		final String userId = LongUtils.toString(user.getId());
		return toUserNameDto(user, discordApiService.getName(userId, guild.getId()));
	}

	public UserNameDto toUserNameDto(@NonNull User user, String name) {
		final UserNameDto userNameDto = UserNameDto.builder().name(name).build();
		ReflectionUtils.shallowCopyFieldState(UserAssembler.toDto(user), userNameDto);
		return userNameDto;
	}
}
