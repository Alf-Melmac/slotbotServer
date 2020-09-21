package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.repository.UserRepository;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {
	private final UserRepository userRepository;

	private User createUser(@NonNull UserDto userDto) {
		User user = UserAssembler.fromDto(userDto);
		return userRepository.save(user);
	}

	User find(UserDto userDto) {
		Optional<User> userOptional = userRepository.findById(LongUtils.parseLong(userDto.getId()));
		return userOptional.orElseGet(() -> createUser(userDto));
	}
}
