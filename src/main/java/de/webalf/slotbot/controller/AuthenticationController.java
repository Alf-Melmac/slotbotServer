package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthenticationController {
	private final GuildService guildService;

	@GetMapping
	public DiscordUserDto getAuthenticatedUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
		return DiscordUserAssembler.toDto(oAuth2User);
	}

	@GetMapping("/access/{requiredApplicationRole}")
	public boolean getAllowedToAccess(@PathVariable String requiredApplicationRole,
	                                  @RequestParam(required = false) String guild) {
		return PermissionHelper.hasPermissionInGuild(
				getByApplicationRole(requiredApplicationRole),
				LongUtils.parseLong(guild, guildService.getCurrentGuildId())
		);
	}
}
