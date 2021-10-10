package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.NotificationSettingAssembler;
import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.controller.NotificationSettingsController;
import de.webalf.slotbot.controller.UserController;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.service.external.DiscordAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static de.webalf.slotbot.service.external.DiscordApiService.isUnknownUser;
import static de.webalf.slotbot.util.ControllerUtils.addLayoutSettings;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.getLoggedInUserId;
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
	private final DiscordAuthenticationService discordAuthenticationService;
	private final UserService userService;
	private final NotificationSettingsService notificationSettingsService;

	@GetMapping("{userId}")
	public ModelAndView getProfile(@PathVariable(value = "userId") String userId) {
		if ("me".equals(userId)) {
			return new ModelAndView("redirect:" + linkTo(methodOn(ProfileWebController.class).getProfileRedirect()).toUri());
		} else if (!onlyNumbers(userId)) {
			throw new ResourceNotFoundException("Profile request with invalid id " + userId);
		}

		ModelAndView mav = new ModelAndView("profile");

		final DiscordApiService.User discordUser = discordApiService.getUser(userId);
		if (isUnknownUser(discordUser)) {
			throw new ResourceNotFoundException("Unknown discord user " + userId);
		}
		mav.addObject("discordUser", discordUser);
		mav.addObject("roles", "@" + String.join(", @", discordAuthenticationService.getRoles(userId)));
		final User user = userService.find(Long.parseLong(userId));
		mav.addObject("participatedEventsCount", userService.getParticipatedEventsCount(user));

		final boolean ownProfile = isLoggedInUser(userId);
		mav.addObject("ownProfile", ownProfile);
		if (ownProfile) {
			mav.addObject("user", UserAssembler.toDto(user));
			mav.addObject("putUserEditableUrl", linkTo(methodOn(UserController.class).updateEventEditable(user.getId(), null, null)).toUri().toString());
			mav.addObject("notificationSettings", NotificationSettingAssembler.toReferencelessDtoList(notificationSettingsService.findSettings(user)));
			mav.addObject("deleteAllByUserUrl", linkTo(methodOn(NotificationSettingsController.class).deleteAllByUser(userId)).toUri().toString());
			mav.addObject("putNotificationSettingsUrl", linkTo(methodOn(NotificationSettingsController.class).updateNotificationSettings(userId, null)).toUri().toString());
		}

		addLayoutSettings(mav);
		return mav;
	}

	@GetMapping("/redirect/me")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public RedirectView getProfileRedirect() {
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(linkTo(methodOn(ProfileWebController.class).getProfile(getLoggedInUserId())).toUri().toString());
		return redirectView;
	}
}
