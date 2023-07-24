package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alf
 * @since 19.11.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl {
	private final UserRepository userRepository;

	User createUser(@NonNull UserDto userDto) {
		User user = UserAssembler.fromDto(userDto);
		return userRepository.save(user);
	}

	/**
	 * Returns the user associated with the given userId. Creates a new user if it doesn't already exist.
	 *
	 * @param id to find or create user for
	 * @return User found by id
	 */
	User find(long id) {
		return userRepository.findById(id)
				.orElseGet(() -> createUser(UserDto.builder().id(LongUtils.toString(id)).build()));
	}
}
