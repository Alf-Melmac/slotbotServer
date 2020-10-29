package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.service.external.DiscordApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alf
 * @since 29.10.2020
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final DiscordApiService discordApiService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();

		Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
		authorities.forEach(grantedAuthority -> {
			if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
				mappedAuthorities.add(getRole(attributes));
			} else {
				mappedAuthorities.add(grantedAuthority);
			}
		});

		oAuth2User = new DefaultOAuth2User(mappedAuthorities, attributes, "username");

		return oAuth2User;
	}

	private GrantedAuthority getRole(Map<String, Object> attributes) {
		return new OAuth2UserAuthority(discordApiService.getRole((String) attributes.get("id")), attributes);
	}
}
