package de.webalf.slotbot.configuration.authentication.website;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 28.09.2023
 */
@Component
public class SessionRegistrator {
	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	/**
	 * Needed for session registry to work
	 *
	 * @see SessionRegistryImpl
	 */
	@Bean
	HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
}
