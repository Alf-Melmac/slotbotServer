package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.assertIsLoggedInUser;

/**
 * @author Alf
 * @since 09.09.2021
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
	private final UserService userService;

	@PostMapping("/editable")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public UserDto updateEventEditable(long pk, String name, String value) {
		final String userId = Long.toString(pk);
		assertIsLoggedInUser(userId);
		UserDto dto = UserDto.builder().id(userId).build();
		try {
			ReflectionUtils.setField(dto.getClass().getDeclaredField(name), dto, value);
		} catch (NoSuchFieldException e) {
			log.error("Can't find field " + name + " while trying to edit it.", e);
			throw BusinessRuntimeException.builder().title(name + " nicht gefunden").cause(e).build();
		}
		return UserAssembler.toDto(userService.update(dto));
	}
}
