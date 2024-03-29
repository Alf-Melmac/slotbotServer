package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.controller.website.FileWebController;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.website.profile.UserOwnProfileDto;
import de.webalf.slotbot.model.dtos.website.profile.UserProfileDto;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.SlotService;
import de.webalf.slotbot.service.UserUpdateService;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.EventCalendarUtil;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.assembler.NotificationSettingAssembler.toReferencelessDtoList;
import static de.webalf.slotbot.service.external.DiscordApiService.isUnknownUser;
import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.getLoggedInUserId;
import static de.webalf.slotbot.util.permissions.PermissionHelper.isLoggedInUser;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 09.09.2021
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final DiscordApiService discordApiService;
	private final GuildUsersService guildUsersService;
	private final UserUpdateService userService;
	private final SlotService slotService;
	private final NotificationSettingsService notificationSettingsService;

	@GetMapping("{userId}")
	public UserProfileDto getProfileInfo(@PathVariable long userId) {
		final DiscordUser discordUser = discordApiService.getUser(Long.toString(userId));
		if (isUnknownUser(discordUser)) {
			throw new ResourceNotFoundException("Unknown discord user " + userId);
		}

		final User user = userService.find(userId);
		final boolean ownProfile = isLoggedInUser(userId);
		return UserProfileDto.builder()
				.user(DiscordUserAssembler.toDto(discordUser))
				.roles(String.join(", ", guildUsersService.getApplicationRoles(userId)))
				.participatedEventsCount(slotService.countByUserBeforeToday(user))
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
				.icsCalendarUrl(linkTo(methodOn(FileWebController.class).getCalendar(EventCalendarUtil.getCalendarName(user.getId()))).toUri().toString())
				.build();
	}

	@PutMapping(value = {"/steamid/", "/steamid/{steamId}"})
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> updateSteamId(@PathVariable(name = "steamId", required = false) String steamId) {
		userService.updateSteamId(steamId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/externalcalendar/{integrationActive}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ResponseEntity<Void> updateExternalCalendarIntegration(@PathVariable(name = "integrationActive") boolean integrationActive) {
		userService.updateSettings(integrationActive);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
