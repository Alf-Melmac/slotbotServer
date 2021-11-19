package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.UserUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.assertIsLoggedInUser;
import static de.webalf.slotbot.util.permissions.PermissionHelper.getLoggedInUserId;

/**
 * @author Alf
 * @since 09.09.2021
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
	private final UserUpdateService userService;

	@PostMapping("/editable")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public UserDto updateUserEditable(long pk, String name, String value) {
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

	@PutMapping("/externalcalendar/{integrationActive}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> updateExternalCalendarIntegration(@PathVariable(name = "integrationActive") boolean integrationActive) {
		final UserDto dto = UserDto.builder().id(getLoggedInUserId()).externalCalendarIntegrationActive(integrationActive).build();
		userService.update(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
