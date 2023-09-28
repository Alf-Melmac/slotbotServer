package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.util.permissions.Role.getByApplicationRole;

/**
 * @author Alf
 * @since 29.07.2022
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
	@GetMapping
	public DiscordUserDto getAuthenticatedUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
		return DiscordUserAssembler.toDto(oAuth2User);
	}

	@GetMapping("/access/{requiredApplicationRole}")
	public boolean getAllowedToAccess(@PathVariable String requiredApplicationRole,
	                                  //Default Value is Guild#GUILD_PLACEHOLDER
	                                  @RequestParam(required = false, defaultValue = "-1") String guild) {
		return PermissionHelper.hasPermissionInGuild(getByApplicationRole(requiredApplicationRole), Long.parseLong(guild));
	}
}
