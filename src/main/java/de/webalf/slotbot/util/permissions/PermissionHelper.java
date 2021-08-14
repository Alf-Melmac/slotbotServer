package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class PermissionHelper {
	public static final String IS_AUTHENTICATED = "isAuthenticated()"; //Isn't anonymous (logged in)

	/**
	 * @throws ForbiddenException if userId doesn't match logged in user
	 */
	public static void assertIsLoggedInUser(String userId) {
		if (!isLoggedInUser(userId)) {
			throw new ForbiddenException("Das darfst du nur für dich selbst.");
		}
	}

	/**
	 * Checks if the given userId matches the currently logged in user
	 *
	 * @param userId to check
	 * @return true if the logged in person has the given user id
	 */
	public static boolean isLoggedInUser(@NonNull String userId) {
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			return userId.equals(oAuth2User.getAttribute("id"));
		}
		return false;
	}
}
