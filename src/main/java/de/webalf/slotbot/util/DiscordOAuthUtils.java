package de.webalf.slotbot.util;

import de.webalf.slotbot.model.enums.DiscordUserObjectFields;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author Alf
 * @since 01.08.2022
 */
@UtilityClass
public final class DiscordOAuthUtils {
	public static String getAttribute(@NonNull OAuth2User user, @NonNull DiscordUserObjectFields field) {
		return user.getAttribute(field.toString());
	}
}
