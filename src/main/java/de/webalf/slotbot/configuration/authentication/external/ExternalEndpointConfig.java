package de.webalf.slotbot.configuration.authentication.external;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Alf
 * @since 04.11.2024
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ExternalEndpointConfig {
	@Bean
	@Order(1)
	SecurityFilterChain externalFilterChain(HttpSecurity http) {
		return http
				// no session management required
				.sessionManagement(sessionManagement -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// only match external requests
				.securityMatcher("/external/**")

				// disable Cross Site Request Forgery token
				// we do not rely on cookie based auth and are completely stateless, so we are safe
				.csrf(CsrfConfigurer::disable)

				.build();
	}
}
