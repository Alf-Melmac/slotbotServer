package de.webalf.slotbot.configuration.authentication.website;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionActivationListener;
import jakarta.servlet.http.HttpSessionEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * DevTools persists sessions across restarts, but doesn't fire session events for the {@link WebsiteSessionAuthorityUpdater}.
 * This marks every session with an {@link HttpSessionActivationListener} so restored sessions can be registered again
 *
 * @author Alf
 * @since 27.10.2026
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class RestoredSessionDevRegistrar {
	private static final String ACTIVATION_LISTENER_ATTRIBUTE = RestoredSessionDevRegistrar.class.getName();
	private static final ActivationListener ACTIVATION_LISTENER = new ActivationListener();

	/**
	 * Sessions restored by Tomcat
	 */
	private static final Queue<HttpSession> RESTORED_SESSIONS = new ConcurrentLinkedQueue<>();

	private final ApplicationEventPublisher eventPublisher;
	private final SessionRegistry sessionRegistry;

	@EventListener
	public void onSessionCreated(@NonNull HttpSessionCreatedEvent event) {
		event.getSession().setAttribute(ACTIVATION_LISTENER_ATTRIBUTE, ACTIVATION_LISTENER);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void registerRestoredSessions() {
		HttpSession session;
		while ((session = RESTORED_SESSIONS.poll()) != null) {
			try {
				final OAuth2AuthenticationToken token = WebsiteSessionAuthorityUpdater.getToken(session);
				if (token != null && token.getPrincipal() instanceof final DiscordOAuthUser user) {
					sessionRegistry.registerNewSession(session.getId(), user);
					eventPublisher.publishEvent(new HttpSessionCreatedEvent(session));
					log.info("Registered restored session");
				}
			} catch (IllegalStateException _) {
				log.info("Failed to register restored session");
			}
		}
	}

	/**
	 * Stored in every session, notified by Tomcat when the session is restored
	 */
	private record ActivationListener() implements HttpSessionActivationListener, Serializable {
		@Override
		public void sessionDidActivate(@NonNull HttpSessionEvent event) {
			RESTORED_SESSIONS.add(event.getSession());
		}
	}
}
