package de.webalf.slotbot.configuration.authentication.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAuthProvider implements AuthenticationProvider {
	private final TokenProvider tokenProvider;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// get the token from the authentication object
		String token = auth.getCredentials().toString();

		SlotbotAuthentication slotbotAuth = new SlotbotAuthentication(token);

		if (token.equals(tokenProvider.getSlotbotKey())) {
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