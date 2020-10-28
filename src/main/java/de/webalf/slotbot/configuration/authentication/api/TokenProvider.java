package de.webalf.slotbot.configuration.authentication.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides token name and key to be used anywhere in the application
 *
 * @author Alf
 * @since 28.10.2020
 */
@Getter
@Component
public class TokenProvider {
	@Value("${slotbot.auth.token.name:slotbot-auth-token}")
	private String tokenName;

	@Value("${slotbot.auth.token}")
	private String slotbotKey;
}
