package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.UserNameDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.service.external.DiscordBotService;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final DiscordBotService discordBotService;
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

	/**
	 * Returns the user associated with the given userId
	 *
	 * @param id to find user for
	 * @return User found by id
	 * @throws ResourceNotFoundException if no user with this userId could be found
	 */
	public User findExisting(long id) {
		return userRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Return the currently {@link PermissionHelper#getLoggedInUserId() logged in user}
	 *
	 * @see #getPotentialLoggedIn()
	 */
	public User getLoggedIn() {
		return findExisting(Long.parseLong(PermissionHelper.getLoggedInUserId()));
	}

	/**
	 * Finds the currently logged-in user or null if no user is logged in
	 *
	 * @see #getLoggedIn()
	 */
	public User getPotentialLoggedIn() {
		final String loggedInUserId = PermissionHelper.getLoggedInUserId();
		if (loggedInUserId.isEmpty()) {
			return null;
		}
		return findExisting(Long.parseLong(loggedInUserId));
	}

	public User getDefaultUser() {
		return find(User.DEFAULT_USER_ID);
	}

	public UserNameDto toUserNameDto(User user, @NonNull Guild guild) {
		if (user == null || user.isDefaultUser()) {
			return null;
		}

		return toUserNameDto(user, discordBotService.getName(user.getId(), guild.getId()));
	}

	private UserNameDto toUserNameDto(@NonNull User user, String name) {
		final UserNameDto userNameDto = UserNameDto.builder().name(name).build();
		ReflectionUtils.shallowCopyFieldState(UserAssembler.toDto(user), userNameDto);
		return userNameDto;
	}
}
