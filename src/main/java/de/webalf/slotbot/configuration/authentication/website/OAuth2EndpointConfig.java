package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.service.BanService;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.web.server.Cookie.SameSite.STRICT;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

/**
 * @author Alf
 * @since 20.10.2020
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2EndpointConfig {
	private final GuildUsersService guildUsersService;
	private final UserService userService;
	private final BanService banService;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final SessionRegistry sessionRegistry;
	private final SessionIpFilter sessionIpFilter;

	@Bean
	SecurityFilterChain oAuthUserFilterChain(HttpSecurity http) throws Exception {
		// https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_i_am_using_angularjs_or_another_javascript_framework
		final CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
		tokenRepository.setCookieCustomizer(cookie -> cookie
				.path("/")
				.httpOnly(false)
				.secure(true)
				.sameSite(STRICT.attributeValue())
		);
		final XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
		// set the name of the attribute the CsrfToken will be populated on
		delegate.setCsrfRequestAttributeName("_csrf");
		// Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
		// default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
		final CsrfTokenRequestHandler requestHandler = delegate::handle;

		http // all non api requests handled here
				.csrf(csrf -> csrf
						.csrfTokenRepository(tokenRepository)
						.csrfTokenRequestHandler(requestHandler))

				.logout(logout -> logout
						.logoutSuccessUrl("/events")
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)))
				)

				.oauth2Login(login -> login
						.loginPage("/oauth2/authorization/discord")
						.defaultSuccessUrl("/events")
						.successHandler(authenticationSuccessHandler)
						.userInfoEndpoint(userInfo -> userInfo
								.userService(oAuthUserService())
						)
				)

				.sessionManagement(session -> session
						.maximumSessions(2).sessionRegistry(sessionRegistry)
				)

				.addFilterBefore(sessionIpFilter, OAuth2LoginAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	CookieSameSiteSupplier sameSiteSupplier() {
		// Force JSESSIONID cookie to be SameSite=Lax
		return CookieSameSiteSupplier.ofLax();
	}

	@Bean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuthUserService() {
		return new CustomOAuth2UserService(guildUsersService, userService, banService);
	}
}
