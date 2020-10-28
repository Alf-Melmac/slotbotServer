package de.webalf.slotbot.configuration.authentication.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenAuthFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
		// check if header contains auth token
		String authToken = request.getHeader(tokenProvider.getTokenName());

		// if there is an auth token, create an Authentication object
		if (authToken != null) {
			Authentication auth = new SlotbotAuthentication(authToken);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		// forward the request
		filterChain.doFilter(request, response);
	}
}