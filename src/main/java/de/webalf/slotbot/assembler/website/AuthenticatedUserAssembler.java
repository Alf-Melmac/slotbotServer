package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.model.dtos.website.AuthenticatedUserDto;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static de.webalf.slotbot.model.enums.DiscordUserObjectFields.*;
import static de.webalf.slotbot.util.DiscordOAuthUtils.getAttribute;
import static de.webalf.slotbot.util.bot.DiscordUserUtils.getAvatarUrl;

/**
 * @author Alf
 * @since 01.08.2022
 */
@UtilityClass
public class AuthenticatedUserAssembler {
	public static AuthenticatedUserDto toDto(OAuth2User oAuth2User) {
		if (oAuth2User == null) {
			return null;
		}
		String id = getAttribute(oAuth2User, ID);
		return AuthenticatedUserDto.builder()
				.id(id)
				.name(getAttribute(oAuth2User, USERNAME))
				.avatarUrl(getAvatarUrl(id, getAttribute(oAuth2User, AVATAR), getAttribute(oAuth2User, DISCRIMINATOR)))
				.build();
	}
}
