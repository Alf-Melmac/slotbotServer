package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alf
 * @since 07.09.2020
 */
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
	private final UserRepository userRepository;

	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable(value = "id") Long userId) {
		return UserAssembler.toDto(userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new));
	}
}
