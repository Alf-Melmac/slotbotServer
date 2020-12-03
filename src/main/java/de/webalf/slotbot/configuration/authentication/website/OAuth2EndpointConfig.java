package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.service.PermissionService;
import de.webalf.slotbot.service.external.DiscordApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Objects;

/**
 * @author Alf
 * @since 20.10.2020
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OAuth2EndpointConfig extends WebSecurityConfigurerAdapter {
	private final DiscordApiService discordApiService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http // all non api requests handled here
				//FIXME: This needs to be changed in future. Currently this allows the frontend to use backend endpoints
				.csrf().disable()

				.authorizeRequests()
				// allow assets and startPage to be accessed by every user
				.antMatchers("/").permitAll()
				.antMatchers("/error/404").permitAll()
				.antMatchers("/assets/**").permitAll()  //JS, CSS, img files
				.antMatchers("/download/*").permitAll() //Downloadable files
				.regexMatchers("^(/discord|/spenden){1}$").permitAll() //Redirects
				.antMatchers("/events/new").hasAnyRole(PermissionService.getEventManageRoleNames())
				.antMatchers("/events/edit/*").hasAnyRole(PermissionService.getEventManageRoleNames())
				.antMatchers("/logs").hasAnyRole(PermissionService.getAdministrativeRoleNames())

				// all other requests must be authenticated
				.anyRequest().authenticated()
				.and()

				.oauth2Login()
				.loginPage("/login").permitAll()
				.tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient())
				.and()
				.userInfoEndpoint().userService(oAuthUserService());
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
		DefaultOAuth2UserService service = new CustomOAuth2UserService(discordApiService);

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
