package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static de.webalf.slotbot.model.enums.DiscordUserObjectFields.*;
import static de.webalf.slotbot.util.DiscordOAuthUtils.getAttribute;
import static de.webalf.slotbot.util.bot.DiscordUserUtils.getAvatarUrl;
import static de.webalf.slotbot.util.permissions.PermissionHelper.getAuthoritiesOfLoggedInUser;

/**
 * @author Alf
 * @since 01.08.2022
 */
@UtilityClass
public class DiscordUserAssembler {
	public static DiscordUserDto toDto(OAuth2User oAuth2User) {
		if (oAuth2User == null) {
			return null;
		}
		final String id = getAttribute(oAuth2User, ID);
		final String globalName = getAttribute(oAuth2User, GLOBAL_NAME);
		return DiscordUserDto.builder()
				.id(id)
				.name(globalName != null ? globalName : getAttribute(oAuth2User, USERNAME))
				.avatarUrl(getAvatarUrl(id, getAttribute(oAuth2User, AVATAR)))
				.authorities(getAuthoritiesOfLoggedInUser())
				.build();
	}

	public static DiscordUserDto toDto(@NonNull DiscordUser user) {
		return DiscordUserDto.builder()
				.id(Long.toString(user.getId()))
				.name(user.getEffectiveName())
				.avatarUrl(user.getAvatarUrl())
				.build();
	}

	public static DiscordUserDto toDto(@NonNull DiscordGuildMember member) {
		final DiscordUser user = member.getUser();
		return DiscordUserDto.builder()
				.id(Long.toString(user.getId()))
				.name(member.getEffectiveName())
				.avatarUrl(member.getAvatarUrl())
				.build();
	}
}
