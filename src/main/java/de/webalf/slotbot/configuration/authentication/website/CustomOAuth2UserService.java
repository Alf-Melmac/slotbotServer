package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.external.discord.DiscordOauthUser;
import de.webalf.slotbot.service.BanService;
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
import tools.jackson.databind.json.JsonMapper;

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
	private final BanService banService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		final Map<String, Object> attributes = oAuth2User.getAttributes();

		final DiscordOauthUser discordUser = getDiscordUser(attributes);
		if (discordUser == null) {
			return oAuth2User;
		}

		//Create user if not existing
		final User user = userService.find(discordUser.getId());

		if (banService.isBanned(user)) {
			throw new ForbiddenException("You're banned.");
		}

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
		return new JsonMapper().convertValue(attributes, DiscordOauthUser.class);
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
			if ("OAUTH2_USER".equals(grantedAuthority.getAuthority())) {
				guildUsersService.getApplicationRoles(discordUser.getId())
						.forEach(role -> mappedAuthorities.add(new OAuth2UserAuthority(role, attributes)));
			} else {
				mappedAuthorities.add(grantedAuthority);
			}
		});

		return mappedAuthorities;
	}
}
