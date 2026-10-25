package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.BanService;
import de.webalf.slotbot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final DiscordOAuthUserService discordOAuthUserService;
	private final UserService userService;
	private final BanService banService;

	@Override
	public @NonNull OAuth2User loadUser(@NonNull OAuth2UserRequest userRequest) {
		final OAuth2User oAuth2User = super.loadUser(userRequest);

		final String id = oAuth2User.getAttribute(DiscordOAuthUser.ID_ATTRIBUTE_KEY);
		if (id == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response"), "Discord user info is missing the id");
		}
		final long userId = Long.parseLong(id);

		//Create user if not existing
		final User user = userService.find(userId);

		if (banService.isBanned(user)) {
			throw new ForbiddenException("You're banned.");
		}

		return discordOAuthUserService.buildUser(userId, oAuth2User.getAttributes());
	}
}
