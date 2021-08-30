package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.controller.NotificationSettingsController;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.service.external.DiscordApiService.GuildMember;
import de.webalf.slotbot.service.external.DiscordApiService.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.isLoggedInUser;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProfileWebController {
	private final DiscordApiService discordApiService;
	private final UserService userService;
	private final NotificationSettingsService notificationSettingsService;

	@GetMapping("{userId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ModelAndView getProfile(@PathVariable(value = "userId") String userId) {
		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());

		final GuildMember guildMember = discordApiService.getGuildMemberWithUser(userId);
		final User user = userService.find(Long.parseLong(userId));
		mav.addObject("user", guildMember);
		mav.addObject("roles", "@" + discordApiService.getRoles(guildMember.getRoles()).stream().map(Role::getName).collect(Collectors.joining(", @")));
		mav.addObject("participatedEventsCount", userService.getParticipatedEventsCount(user));

		final boolean ownProfile = isLoggedInUser(userId);
		mav.addObject("ownProfile", ownProfile);
		if (ownProfile) {
			mav.addObject("notificationSettings", notificationSettingsService.findSettings(user));
			mav.addObject("deleteAllByUserUrl", linkTo(methodOn(NotificationSettingsController.class).deleteAllByUser(userId)).toUri().toString());
			mav.addObject("putNotificationSettingsUrl", linkTo(methodOn(NotificationSettingsController.class).updateNotificationSettings(userId, null)).toUri().toString());
		}

		return mav;
	}
}
