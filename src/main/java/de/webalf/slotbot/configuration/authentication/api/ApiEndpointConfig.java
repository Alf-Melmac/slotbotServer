package de.webalf.slotbot.configuration.authentication.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ApiEndpointConfig {
	private final TokenAuthFilter tokenAuthFilter;
	private final TokenAuthProvider tokenAuthProvider;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	protected SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
				// no session management required
				.sessionManagement(sessionManagement -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// only match API requests
				.securityMatcher(API + "/**")

				// disable Cross Site Request Forgery token
				// we do not rely on cookie based auth and are completely stateless, so we are safe
				.csrf(CsrfConfigurer::disable)

				// authentication for token based authentication
				.authenticationProvider(tokenAuthProvider)
				.addFilterBefore(tokenAuthFilter, BasicAuthenticationFilter.class);

		return http.build();
	}
}