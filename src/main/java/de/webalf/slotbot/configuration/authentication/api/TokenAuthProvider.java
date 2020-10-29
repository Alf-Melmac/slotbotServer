package de.webalf.slotbot.configuration.authentication.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Component
@Slf4j
public class TokenAuthProvider implements AuthenticationProvider {
	@Value("${slotbot.auth.token}")
	private String slotbotKey;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// get the token from the authentication object
		String token = auth.getCredentials().toString();

		SlotbotAuthentication slotbotAuth = new SlotbotAuthentication(token);

		if (token.equals(slotbotKey)) {
			slotbotAuth.setAuthenticated(true);
		} else {
			log.warn("Invalid token " + token);
			throw new BadCredentialsException("Invalid token " + token);
		}

		return slotbotAuth;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return (SlotbotAuthentication.class.isAssignableFrom(arg0));
	}
}