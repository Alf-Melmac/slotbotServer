package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.model.event.GuildUserEvent;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionIdChangedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * Updates the {@link GrantedAuthority authorities} stored in every session of a user whose roles changed, so the user
 * doesn't have to log in again
 *
 * @author Alf
 * @since 19.10.2026
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebsiteSessionAuthorityUpdater {
	private final DiscordOAuthUserService discordOAuthUserService;

	/**
	 * All live sessions by their id
	 */
	private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

	@EventListener
	public void onSessionCreated(@NonNull HttpSessionCreatedEvent event) {
		final HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
	}

	@EventListener
	public void onSessionIdChanged(@NonNull HttpSessionIdChangedEvent event) {
		sessions.remove(event.getOldSessionId());
		final HttpSession session = (HttpSession) event.getSource();
		sessions.put(session.getId(), session);
	}

	@EventListener
	public void onSessionDestroyed(@NonNull HttpSessionDestroyedEvent event) {
		sessions.remove(event.getId());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT, fallbackExecution = true) // Roles query must see the new roles
	public void onGuildUserEvent(@NonNull GuildUserEvent event) {
		final long userId = event.userId();
		final Set<String> sessionIds = discordOAuthUserService.findSessions(userId)
				.map(SessionInformation::getSessionId)
				.collect(Collectors.toUnmodifiableSet());
		if (!sessionIds.isEmpty()) {
			writeAuthorities(userId, sessionIds);
		}
	}

	/**
	 * Stores a new security context with the current authorities in all the given sessions
	 *
	 * @param userId     discord id of user whose roles changed
	 * @param sessionIds user's sessions
	 */
	@Synchronized // Concurrent role changes must not overwrite newer authorities with an older roles query result
	private void writeAuthorities(long userId, Set<String> sessionIds) {
		for (final String sessionId : sessionIds) { //NOSONAR java:S135
			final HttpSession session = sessions.get(sessionId);
			if (session == null) {
				continue;
			}
			final OAuth2AuthenticationToken oldToken = getToken(session);
			if (oldToken == null) {
				continue;
			}
			final DiscordOAuthUser updatedUser = discordOAuthUserService.buildUser(userId, oldToken.getPrincipal().getAttributes());

			final Collection<? extends GrantedAuthority> updatedAuthorities = updatedUser.getAuthorities();
			// Copies all other token state, e.g. the details needed by SessionIpFilter
			final OAuth2AuthenticationToken updatedToken = oldToken.toBuilder()
					.principal(updatedUser)
					.authorities(authorities -> {
						// Only replace our application roles, keep authorities added by Spring on login (e.g. FACTOR_AUTHORIZATION_CODE)
						authorities.removeAll(oldToken.getPrincipal().getAuthorities());
						authorities.addAll(updatedAuthorities);
					})
					.build();
			try {
				session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(updatedToken));
				log.trace("Updated authorities of session {} for user {}", sessionId, userId);
			} catch (IllegalStateException _) {
				log.trace("Session {} of user {} has been invalidated in the meantime", sessionId, userId);
			}
		}
	}

	/**
	 * Returns the authentication of the given session
	 *
	 * @param session to read from
	 * @return stored token, or {@code null} if there is no session, it has been invalidated or has no context yet (during login)
	 */
	private static OAuth2AuthenticationToken getToken(@NonNull HttpSession session) {
		try {
			return session.getAttribute(SPRING_SECURITY_CONTEXT_KEY) instanceof final SecurityContext context
					&& context.getAuthentication() instanceof final OAuth2AuthenticationToken token ? token : null;
		} catch (IllegalStateException _) {
			log.trace("Session {} already invalidated while fetching token", session.getId());
			return null;
		}
	}
}
