package de.webalf.slotbot.configuration.authentication.api;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.authentication.ApiToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.util.permissions.PermissionHelper.buildGuildAuthenticationWithPrefix;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Component
@Slf4j
public class TokenAuthFilter extends OncePerRequestFilter {
	@Value("${slotbot.auth.token.name:slotbot-auth-token}")
	@Getter
	private String tokenName;

	private final TokenAuthProvider tokenAuthProvider;
	private final HandlerExceptionResolver resolver;

	public TokenAuthFilter(TokenAuthProvider tokenAuthProvider, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.tokenAuthProvider = tokenAuthProvider;
		this.resolver = resolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
		// check if header contains auth token
		final String authToken = request.getHeader(tokenName);

		// if there is an auth token, create an Authentication object
		if (authToken != null) {
			log.info("{} API request to '{}' with token '{}' from: {}", request.getMethod(), request.getRequestURL(), authToken, request.getHeader("user-agent"));
			final ApiToken apiToken;
			try {
				apiToken = tokenAuthProvider.getApiToken(authToken);
			} catch (ForbiddenException ex) {
				resolver.resolveException(request, response, null, ex);
				return;
			}
			final Set<GrantedAuthority> grantedAuthorities = mapAuthorities(apiToken);
			log.debug("Token '{}' granted {}", authToken, grantedAuthorities);
			final Authentication auth = new SlotbotAuthentication(authToken, grantedAuthorities, apiToken.getGuild().getId());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		// forward the request
		filterChain.doFilter(request, response);
	}

	private Set<GrantedAuthority> mapAuthorities(@NonNull ApiToken apiToken) {
		final String tokenTypeName = apiToken.getType().name();
		return Set.of(new SimpleGrantedAuthority(ROLE_PREFIX + tokenTypeName),
				new SimpleGrantedAuthority(buildGuildAuthenticationWithPrefix(tokenTypeName, apiToken.getGuild())));
	}
}
