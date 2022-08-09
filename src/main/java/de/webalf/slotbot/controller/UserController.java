package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.profile.UserOwnProfileDto;
import de.webalf.slotbot.model.dtos.website.profile.UserProfileDto;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.UserUpdateService;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.service.external.DiscordAuthenticationService;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.assembler.NotificationSettingAssembler.toReferencelessDtoList;
import static de.webalf.slotbot.service.external.DiscordApiService.isUnknownUser;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.*;

/**
 * @author Alf
 * @since 09.09.2021
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
	private final DiscordApiService discordApiService;
	private final DiscordAuthenticationService discordAuthenticationService;
	private final UserUpdateService userService;
	private final NotificationSettingsService notificationSettingsService;

	@GetMapping("{userId}")
	public UserProfileDto getProfileInfo(@PathVariable long userId) {
		final DiscordApiService.User discordUser = discordApiService.getUser(Long.toString(userId));
		if (isUnknownUser(discordUser)) {
			throw new ResourceNotFoundException("Unknown discord user " + userId);
		}

		final User user = userService.find(userId);
		final boolean ownProfile = isLoggedInUser(userId);
		return UserProfileDto.builder()
				.user(DiscordUserAssembler.toDto(discordUser))
				.roles("@" + String.join(", @", discordAuthenticationService.getRoles(userId)))
				.participatedEventsCount(user.countParticipatedEvents())
				.ownProfile(ownProfile)
				.build();
	}

	@GetMapping("/own")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public UserOwnProfileDto getOwnProfileInfo() {
		User user = userService.find(getLoggedInUserId());

		return UserOwnProfileDto.builder()
				.steamId64(LongUtils.toString(user.getSteamId64()))
				.notificationSettings(toReferencelessDtoList(notificationSettingsService.findSettings(user)))
				.externalCalendarIntegrationActive(user.isExternalCalendarIntegrationActive())
				.build();
	}

	@PostMapping("/editable")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	@Deprecated
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

	@PutMapping("/steamid/{steamId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> updateSteamId(@PathVariable(name = "steamId") String steamId) {
		final UserDto dto = UserDto.builder().id(getLoggedInUserId()).steamId64(steamId).build();
		userService.update(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/externalcalendar/{integrationActive}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> updateExternalCalendarIntegration(@PathVariable(name = "integrationActive") boolean integrationActive) {
		final UserDto dto = UserDto.builder().id(getLoggedInUserId()).externalCalendarIntegrationActive(integrationActive).build();
		userService.update(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
