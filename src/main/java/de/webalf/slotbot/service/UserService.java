package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.UserNameDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final DiscordApiService discordApiService;
	private final UserServiceImpl userServiceImpl;

	User find(@NonNull UserDto userDto) {
		return userRepository.findById(LongUtils.parseLong(userDto.getId()))
				.orElseGet(() -> userServiceImpl.createUser(userDto));
	}

	/**
	 * @see UserServiceImpl#find(long)
	 */
	public User find(long id) {
		return userServiceImpl.find(id);
	}

	Optional<User> findExistingOptional(long id) {
		return userRepository.findById(id);
	}

	/**
	 * Returns the user associated with the given userId
	 *
	 * @param id to find user for
	 * @return User found by id
	 * @throws ResourceNotFoundException if no user with this userId could be found
	 */
	User findExisting(long id) {
		return findExistingOptional(id).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Return the currently {@link PermissionHelper#getLoggedInUserId() logged in user}
	 */
	public User getLoggedIn() {
		return findExisting(Long.parseLong(PermissionHelper.getLoggedInUserId()));
	}

	public User getDefaultUser() {
		return find(User.DEFAULT_USER_ID);
	}

	public UserNameDto toUserNameDto(User user, @NonNull Guild guild) {
		if (user == null || user.isDefaultUser()) {
			return null;
		}

		final String userId = LongUtils.toString(user.getId());
		return toUserNameDto(user, discordApiService.getName(userId, guild.getId()));
	}

	private UserNameDto toUserNameDto(@NonNull User user, String name) {
		final UserNameDto userNameDto = UserNameDto.builder().name(name).build();
		ReflectionUtils.shallowCopyFieldState(UserAssembler.toDto(user), userNameDto);
		return userNameDto;
	}
}
