package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth user identified by its discord id. Unlike {@link DefaultOAuth2User}, equality ignores the authorities, so the
 * user stays the same {@link SessionRegistry} principal when its authorities are refreshed.
 * <p>
 * Attributes are the user object returned by the discord oauth2 flow
 *
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object">Discord User Object</a>
 * @since 27.01.2023
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class DiscordOAuthUser extends DefaultOAuth2User {
	static final String ID_ATTRIBUTE_KEY = "id";
	static final String NAME_ATTRIBUTE_KEY = "username";
	private static final String GLOBAL_NAME_ATTRIBUTE_KEY = "global_name";
	private static final String AVATAR_ATTRIBUTE_KEY = "avatar";

	/**
	 * Discord id of the user
	 */
	private final long id;

	/**
	 * @see DiscordOAuthUserService#buildUser(long, Map)
	 */
	DiscordOAuthUser(long id, Collection<? extends GrantedAuthority> authorities, @NonNull Map<String, Object> attributes) {
		super(authorities, attributes, NAME_ATTRIBUTE_KEY);
		this.id = id;
	}

	/**
	 * @see DiscordUserUtils#getEffectiveName(String, String)
	 */
	public String getEffectiveName() {
		return DiscordUserUtils.getEffectiveName(getAttribute(GLOBAL_NAME_ATTRIBUTE_KEY), getName());
	}

	/**
	 * @see DiscordUserUtils#getAvatarUrl(long, String)
	 */
	public String getAvatarUrl() {
		return DiscordUserUtils.getAvatarUrl(id, getAttribute(AVATAR_ATTRIBUTE_KEY));
	}
}
