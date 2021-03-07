package de.webalf.slotbot.configuration.authentication.api;

import de.webalf.slotbot.model.authentication.ApiToken;
import de.webalf.slotbot.repository.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAuthProvider implements AuthenticationProvider {
	private final ApiTokenRepository apiTokenRepository;

	@Override
	public Authentication authenticate(Authentication auth) {
		return auth; //If I remove this method I get a NullPointerException from AbstractSecurityInterceptor#authenticateIfRequired. Code never dies
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return (SlotbotAuthentication.class.isAssignableFrom(arg0));
	}

	ApiToken getApiToken(String token) {
		return apiTokenRepository.findById(token)
				.orElseThrow(() -> new BadCredentialsException("Invalid token " + token));
	}
}