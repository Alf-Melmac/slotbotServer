package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.service.external.DiscordApiService.GuildMember;
import de.webalf.slotbot.service.external.DiscordApiService.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;
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

	@GetMapping("{userId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public ModelAndView getProfile(@PathVariable(value = "userId") String userId) {
		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());

		final GuildMember guildMember = discordApiService.getGuildMemberWithUser(userId);
		mav.addObject("user", guildMember);
		mav.addObject("roles", "@" + discordApiService.getRoles(guildMember.getRoles()).stream().map(Role::getName).collect(Collectors.joining(", @")));

		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			mav.addObject("ownProfile", userId.equals(oAuth2User.getAttribute("id")));
		}

		return mav;
	}
}
