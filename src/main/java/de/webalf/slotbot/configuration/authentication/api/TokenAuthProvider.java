package de.webalf.slotbot.configuration.authentication.api;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.authentication.ApiToken;
import de.webalf.slotbot.repository.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthProvider implements AuthenticationProvider {
	private final ApiTokenRepository apiTokenRepository;

	@Override
	public Authentication authenticate(Authentication auth) {
		return auth;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return (SlotbotAuthentication.class.isAssignableFrom(arg0));
	}

	ApiToken getApiToken(String token) throws ForbiddenException {
		return apiTokenRepository.findById(token)
				.orElseThrow(() -> {
					log.warn("Received request with invalid token {}", token);
					return new ForbiddenException("Invalid token '" + token + "'");
				});
	}
}