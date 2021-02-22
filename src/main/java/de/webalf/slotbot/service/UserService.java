package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.UserNameDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.DtoUtils;
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

	private User createUser(@NonNull UserDto userDto) {
		User user = UserAssembler.fromDto(userDto);
		return userRepository.save(user);
	}

	public User update(UserDto userDto) {
		User user = find(LongUtils.parseLong(userDto.getId()));

		DtoUtils.ifPresentParse(userDto.getSteamId64(), user::setSteamId64);

		return user;
	}

	User find(UserDto userDto) {
		return userRepository.findById(LongUtils.parseLong(userDto.getId()))
				.orElseGet(() -> createUser(userDto));
	}

	User find(long id) {
		return userRepository.findById(id)
				.orElseGet(() -> createUser(UserDto.builder().id(LongUtils.toString(id)).build()));
	}

	public UserNameDto toUserNameDto(User user) {
		if (user == null || user.isDefaultUser()) {
			return null;
		}

		final String userId = LongUtils.toString(user.getId());
		final UserNameDto userNameDto = UserNameDto.builder().name(discordApiService.getName(userId)).build();

		ReflectionUtils.shallowCopyFieldState(UserAssembler.toDto(user), userNameDto);
		return userNameDto;
	}
}
