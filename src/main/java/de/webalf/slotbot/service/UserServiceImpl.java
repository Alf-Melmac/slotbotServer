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

/**
 * @author Alf
 * @since 19.11.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl {
	private final UserRepository userRepository;

	User createUser(@NonNull UserDto userDto) {
		User user = UserAssembler.fromDto(userDto);
		return userRepository.save(user);
	}

	User find(long id) {
		return userRepository.findById(id)
				.orElseGet(() -> createUser(UserDto.builder().id(LongUtils.toString(id)).build()));
	}
}
