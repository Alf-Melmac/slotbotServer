package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.service.external.DiscordAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Objects;

/**
 * @author Alf
 * @since 20.10.2020
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OAuth2EndpointConfig {
	private final DiscordAuthenticationService discordAuthenticationService;

	@Bean
	protected SecurityFilterChain oAuthUserFilterChain(HttpSecurity http) throws Exception {
		// https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_i_am_using_angularjs_or_another_javascript_framework
		final CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		tokenRepository.setCookiePath("/");
		final XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
		// set the name of the attribute the CsrfToken will be populated on
		delegate.setCsrfRequestAttributeName("_csrf");
		// Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
		// default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
		final CsrfTokenRequestHandler requestHandler = delegate::handle;

		http // all non api requests handled here
				.cors().and()
				.csrf(csrf -> csrf
						.csrfTokenRepository(tokenRepository)
						.csrfTokenRequestHandler(requestHandler))

				.logout()
				.logoutSuccessUrl("/events")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.deleteCookies("JSESSIONID")
				.and()

				.oauth2Login()
				.loginPage("/oauth2/authorization/discord")
				.defaultSuccessUrl("/events")
				.tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient())
				.and()
				.userInfoEndpoint().userService(oAuthUserService());

		return http.build();
	}

	@Bean
	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();

		client.setRequestEntityConverter(new OAuth2AuthorizationCodeGrantRequestEntityConverter() {
			@Override
			public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest oauth2Request) {
				return withUserAgent(Objects.requireNonNull(super.convert(oauth2Request)));
			}
		});

		return client;
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuthUserService() {
		DefaultOAuth2UserService service = new CustomOAuth2UserService(discordAuthenticationService);

		service.setRequestEntityConverter(new OAuth2UserRequestEntityConverter() {
			@Override
			public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
				return withUserAgent(Objects.requireNonNull(super.convert(userRequest)));
			}
		});

		return service;
	}

	private static final String DISCORD_BOT_USER_AGENT = "Discord-OAuth";

	private static RequestEntity<?> withUserAgent(RequestEntity<?> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(request.getHeaders());
		headers.add(HttpHeaders.USER_AGENT, DISCORD_BOT_USER_AGENT);

		return new RequestEntity<>(request.getBody(), headers, request.getMethod(), request.getUrl());
	}
}
