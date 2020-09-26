package de.webalf.slotbot.security;

import de.webalf.slotbot.security.authentication.TokenAuthFilter;
import de.webalf.slotbot.security.authentication.TokenAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final TokenAuthFilter tokenAuthFilter;
	private final TokenAuthProvider tokenAuthProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// no session management required
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()

				// the following URLs should be permitted without any authentication
				.authorizeRequests()
				.antMatchers("/status").permitAll()
				.antMatchers("/*").permitAll()
				.antMatchers("/assets/**").permitAll()

				// all other requests must be authenticated
				.anyRequest().fullyAuthenticated()
				.and()

				// disable Cross Site Request Forgery token
				// we do not rely on cookie based auth and are completely stateless so we are safe
				.csrf().disable()

				// authentication for token based authentication
				.authenticationProvider(tokenAuthProvider)
				.addFilterBefore(tokenAuthFilter, BasicAuthenticationFilter.class);
	}
}