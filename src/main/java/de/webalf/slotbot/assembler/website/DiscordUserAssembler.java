package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.configuration.authentication.website.DiscordOAuthUser;
import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import static de.webalf.slotbot.util.permissions.PermissionHelper.getAuthoritiesOfLoggedInUser;

/**
 * @author Alf
 * @since 01.08.2022
 */
@UtilityClass
public class DiscordUserAssembler {
	public static DiscordUserDto toDto(DiscordOAuthUser user) {
		if (user == null) {
			return null;
		}
		return DiscordUserDto.builder()
				.id(Long.toString(user.getId()))
				.name(user.getEffectiveName())
				.avatarUrl(user.getAvatarUrl())
				.authorities(getAuthoritiesOfLoggedInUser())
				.build();
	}

	public static DiscordUserDto toDto(@NonNull DiscordUser user) {
		return DiscordUserDto.builder()
				.id(Long.toString(user.id()))
				.name(user.getEffectiveName())
				.avatarUrl(user.getAvatarUrl())
				.build();
	}

	public static DiscordUserDto toDto(@NonNull DiscordGuildMember member) {
		final DiscordUser user = member.user();
		return DiscordUserDto.builder()
				.id(Long.toString(user.id()))
				.name(member.getEffectiveName())
				.avatarUrl(member.getAvatarUrl())
				.build();
	}
}
