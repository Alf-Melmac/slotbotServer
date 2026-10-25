package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.model.event.GuildUserCreatedEvent;
import de.webalf.slotbot.model.event.GuildUserDeleteEvent;
import de.webalf.slotbot.model.event.GuildUserEvent;
import de.webalf.slotbot.model.event.GuildUserRoleUpdateEvent;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.util.permissions.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionIdChangedEvent;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static de.webalf.slotbot.util.permissions.PermissionHelper.buildAuthenticationWithPrefix;
import static de.webalf.slotbot.util.permissions.PermissionHelper.buildGuildAuthenticationWithPrefix;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @author Alf
 * @since 19.10.2026
 */
@ExtendWith(MockitoExtension.class)
class WebsiteSessionAuthorityUpdaterTest {
	private static final long GUILD_ID = 1L;
	private static final long USER_ID = 100L;
	private static final long OTHER_USER_ID = 200L;
	private static final String REGISTRATION_ID = "discord";
	private static final String OLD_ROLE = buildAuthenticationWithPrefix(Role.EVENT_MANAGE.getApplicationRole());
	private static final String NEW_ROLE = buildGuildAuthenticationWithPrefix(Role.ADMINISTRATOR.getApplicationRole(), GUILD_ID);

	@Mock
	private GuildUsersService guildUsersService;

	private SessionRegistryImpl sessionRegistry;
	private WebsiteSessionAuthorityUpdater sut;

	@BeforeEach
	void setUp() {
		sessionRegistry = new SessionRegistryImpl();
		sut = new WebsiteSessionAuthorityUpdater(new DiscordOAuthUserService(sessionRegistry, guildUsersService));
	}

	@Test
	void onGuildUserRoleUpdateEventReplacesContextWithNewAuthorities() {
		final MockHttpSession session = createLoggedInSession(USER_ID);
		final SecurityContext oldContext = getContext(session);
		final OAuth2AuthenticationToken oldToken = getToken(oldContext);
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(roleUpdateEvent());

		final SecurityContext newContext = getContext(session);
		assertThat(newContext).isNotSameAs(oldContext);
		assertThat(oldContext.getAuthentication()).isSameAs(oldToken);
		assertThat(AuthorityUtils.authorityListToSet(oldToken.getAuthorities())).containsExactly(OLD_ROLE);

		final OAuth2AuthenticationToken newToken = getToken(newContext);
		assertThat(AuthorityUtils.authorityListToSet(newToken.getAuthorities())).containsExactly(NEW_ROLE);
		assertThat(AuthorityUtils.authorityListToSet(newToken.getPrincipal().getAuthorities())).containsExactly(NEW_ROLE);
		assertThat(newToken.getPrincipal().getAttributes()).isEqualTo(oldToken.getPrincipal().getAttributes());
		assertThat(newToken.getDetails()).isInstanceOf(WebAuthenticationDetails.class).isSameAs(oldToken.getDetails());
		assertThat(newToken.getAuthorizedClientRegistrationId()).isEqualTo(REGISTRATION_ID);
		assertThat(newToken.isAuthenticated()).isTrue();
	}

	@ParameterizedTest
	@MethodSource("guildUserEvents")
	void onGuildUserEventUpdatesContext(GuildUserEvent event) {
		final MockHttpSession session = createLoggedInSession(USER_ID);
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(event);

		assertThat(getAuthorities(session)).containsExactly(NEW_ROLE);
	}

	private static Stream<GuildUserEvent> guildUserEvents() {
		return Stream.of(
				roleUpdateEvent(),
				new GuildUserCreatedEvent(GUILD_ID, USER_ID, Role.ADMINISTRATOR),
				new GuildUserDeleteEvent(GUILD_ID, USER_ID, Role.EVENT_MANAGE));
	}

	@Test
	void onGuildUserRoleUpdateEventUpdatesSessionWithChangedId() {
		final MockHttpSession session = new MockHttpSession();
		sut.onSessionCreated(new HttpSessionCreatedEvent(session));
		final String oldId = session.getId();
		session.changeSessionId();
		sut.onSessionIdChanged(new HttpSessionIdChangedEvent(session, oldId));
		login(session, DiscordOAuth2UserTestFactory.create(USER_ID, OLD_ROLE));
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getAuthorities(session)).containsExactly(NEW_ROLE);
	}

	@Test
	void onSessionIdChangedForgetsOldId() {
		final MockHttpSession session = new MockHttpSession();
		sut.onSessionCreated(new HttpSessionCreatedEvent(session));
		final String oldId = session.getId();
		session.changeSessionId();
		sut.onSessionIdChanged(new HttpSessionIdChangedEvent(session, oldId));
		final DiscordOAuthUser user = DiscordOAuth2UserTestFactory.create(USER_ID, OLD_ROLE);
		login(session, user);
		sessionRegistry.registerNewSession(oldId, user);
		final SecurityContext oldContext = getContext(session);

		// Destroy event only carries the new id, so the old id must have been removed on change
		sut.onSessionDestroyed(new HttpSessionDestroyedEvent(session));
		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getContext(session)).isSameAs(oldContext);
		verify(guildUsersService, never()).getApplicationRoles(anyLong());
	}

	@Test
	void onGuildUserRoleUpdateEventUpdatesAllSessionsOfUserWithTheirOwnAttributes() {
		final MockHttpSession session1 = createLoggedInSession(DiscordOAuth2UserTestFactory.createWithName(USER_ID, "oldName", OLD_ROLE));
		final MockHttpSession session2 = createLoggedInSession(DiscordOAuth2UserTestFactory.createWithName(USER_ID, "newName", OLD_ROLE));
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getAuthorities(session1)).containsExactly(NEW_ROLE);
		assertThat(getAuthorities(session2)).containsExactly(NEW_ROLE);
		assertThat(getToken(getContext(session1)).getPrincipal().getName()).isEqualTo("oldName");
		assertThat(getToken(getContext(session2)).getPrincipal().getName()).isEqualTo("newName");
	}

	@Test
	void onGuildUserRoleUpdateEventIgnoresOtherUsersSessions() {
		final MockHttpSession session = createLoggedInSession(USER_ID);
		final MockHttpSession otherSession = createLoggedInSession(OTHER_USER_ID);
		final SecurityContext otherContext = getContext(otherSession);
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getAuthorities(session)).containsExactly(NEW_ROLE);
		assertThat(getContext(otherSession)).isSameAs(otherContext);
		assertThat(getAuthorities(otherSession)).containsExactly(OLD_ROLE);
		verify(guildUsersService, never()).getApplicationRoles(OTHER_USER_ID);
	}

	@Test
	void onGuildUserRoleUpdateEventSkipsSessionWithoutContext() {
		final MockHttpSession session = new MockHttpSession();
		sut.onSessionCreated(new HttpSessionCreatedEvent(session));
		sessionRegistry.registerNewSession(session.getId(), DiscordOAuth2UserTestFactory.create(USER_ID, OLD_ROLE));

		assertThatNoException().isThrownBy(() -> sut.onGuildUserEvent(roleUpdateEvent()));

		assertThat(session.getAttribute(SPRING_SECURITY_CONTEXT_KEY)).isNull();
		verifyNoInteractions(guildUsersService);
	}

	@Test
	void onGuildUserRoleUpdateEventUpdatesLoggedInSessionAfterSkippedSessions() {
		final MockHttpSession sessionWithoutContext = new MockHttpSession();
		sut.onSessionCreated(new HttpSessionCreatedEvent(sessionWithoutContext));
		sessionRegistry.registerNewSession(sessionWithoutContext.getId(), DiscordOAuth2UserTestFactory.create(USER_ID, OLD_ROLE));
		final MockHttpSession destroyedSession = createLoggedInSession(USER_ID);
		sut.onSessionDestroyed(new HttpSessionDestroyedEvent(destroyedSession));
		final MockHttpSession session = createLoggedInSession(USER_ID);
		when(guildUsersService.getApplicationRoles(USER_ID)).thenReturn(Set.of(NEW_ROLE));

		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getAuthorities(session)).containsExactly(NEW_ROLE);
	}

	@Test
	void onGuildUserRoleUpdateEventIgnoresInvalidatedSession() {
		final MockHttpSession session = createLoggedInSession(USER_ID);
		session.invalidate();

		assertThatNoException().isThrownBy(() -> sut.onGuildUserEvent(roleUpdateEvent()));

		verifyNoInteractions(guildUsersService);
	}

	@Test
	void onGuildUserRoleUpdateEventIgnoresDestroyedSession() {
		final MockHttpSession session = createLoggedInSession(USER_ID);
		final SecurityContext oldContext = getContext(session);

		sut.onSessionDestroyed(new HttpSessionDestroyedEvent(session));
		sut.onGuildUserEvent(roleUpdateEvent());

		assertThat(getContext(session)).isSameAs(oldContext);
		verify(guildUsersService, never()).getApplicationRoles(anyLong());
	}

	private static GuildUserRoleUpdateEvent roleUpdateEvent() {
		return new GuildUserRoleUpdateEvent(GUILD_ID, USER_ID, Role.EVENT_MANAGE, Role.ADMINISTRATOR);
	}

	private MockHttpSession createLoggedInSession(long userId) {
		return createLoggedInSession(DiscordOAuth2UserTestFactory.create(userId, OLD_ROLE));
	}

	private MockHttpSession createLoggedInSession(DiscordOAuthUser user) {
		final MockHttpSession session = new MockHttpSession();
		sut.onSessionCreated(new HttpSessionCreatedEvent(session));
		login(session, user);
		return session;
	}

	private void login(MockHttpSession session, DiscordOAuthUser user) {
		sessionRegistry.registerNewSession(session.getId(), user);

		final OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(user, user.getAuthorities(), REGISTRATION_ID);
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		token.setDetails(new WebAuthenticationDetails(request));
		session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));
	}

	private static SecurityContext getContext(MockHttpSession session) {
		return (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
	}

	private static Set<String> getAuthorities(MockHttpSession session) {
		return AuthorityUtils.authorityListToSet(getToken(getContext(session)).getAuthorities());
	}

	private static OAuth2AuthenticationToken getToken(SecurityContext context) {
		assertThat(context.getAuthentication()).isInstanceOf(OAuth2AuthenticationToken.class);
		return (OAuth2AuthenticationToken) Objects.requireNonNull(context.getAuthentication());
	}
}
