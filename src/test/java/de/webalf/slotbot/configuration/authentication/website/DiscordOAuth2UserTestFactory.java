package de.webalf.slotbot.configuration.authentication.website;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Map;

/**
 * @author Alf
 * @since 19.10.2026
 */
@UtilityClass
public final class DiscordOAuth2UserTestFactory {
	/**
	 * Creates a user with the given authorities
	 */
	public static DiscordOAuthUser create(long id, String... authorities) {
		return createWithName(id, "user" + id, authorities);
	}

	/**
	 * Creates a user with the given name and authorities
	 */
	public static DiscordOAuthUser createWithName(long id, String name, String... authorities) {
		return new DiscordOAuthUser(id, AuthorityUtils.createAuthorityList(authorities), Map.of(DiscordOAuthUser.ID_ATTRIBUTE_KEY, Long.toString(id), DiscordOAuthUser.NAME_ATTRIBUTE_KEY, name));
	}
}
