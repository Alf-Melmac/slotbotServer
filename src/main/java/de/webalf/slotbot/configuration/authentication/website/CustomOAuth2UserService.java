package de.webalf.slotbot.configuration.authentication.website;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.webalf.slotbot.model.external.discord.DiscordOauthUser;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alf
 * @since 29.10.2020
 */
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final GuildUsersService guildUsersService;
	private final UserService userService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		final Map<String, Object> attributes = oAuth2User.getAttributes();

		final DiscordOauthUser discordUser = getDiscordUser(attributes);
		if (discordUser == null) {
			return oAuth2User;
		}

		//Create user if not existing
		userService.find(discordUser.getId());
		final Collection<? extends GrantedAuthority> mappedAuthorities = mapAuthorities(attributes, discordUser, oAuth2User.getAuthorities());
		oAuth2User = new DefaultOAuth2User(mappedAuthorities, attributes, "username");

		return oAuth2User;
	}

	/**
	 * Returns the {@link DiscordOauthUser} for the given json map
	 *
	 * @param attributes of the user object
	 * @return user object
	 */
	private static DiscordOauthUser getDiscordUser(Map<String, Object> attributes) {
		DiscordOauthUser discordUser;
		try {
			final String attributesAsJson = new ObjectMapper().writeValueAsString(attributes);
			discordUser = new ObjectMapper().readValue(attributesAsJson, DiscordOauthUser.class);
		} catch (IOException e) {
			log.error("Failed to read discordUser", e);
			return null;
		}
		return discordUser;
	}

	/**
	 * Sets the {@link GrantedAuthority}s for the given {@link DiscordOauthUser}
	 *
	 * @param attributes  of the discordUser
	 * @param discordUser user
	 * @param authorities existing authorities
	 * @return set of mapped authorities
	 */
	private Set<GrantedAuthority> mapAuthorities(Map<String, Object> attributes, DiscordOauthUser discordUser, Collection<? extends GrantedAuthority> authorities) {
		final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

		authorities.forEach(grantedAuthority -> {
			if (grantedAuthority.getAuthority().equals("OAUTH2_USER")) {
				guildUsersService.getApplicationRoles(discordUser.getId())
						.forEach(role -> mappedAuthorities.add(new OAuth2UserAuthority(role, attributes)));
			} else {
				mappedAuthorities.add(grantedAuthority);
			}
		});

		return mappedAuthorities;
	}
}
