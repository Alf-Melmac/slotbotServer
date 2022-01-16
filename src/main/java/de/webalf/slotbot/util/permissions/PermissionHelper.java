package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Guild;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.Role.EVENT_MANAGE;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class PermissionHelper {
	public static final String IS_AUTHENTICATED = "isAuthenticated()"; //Isn't anonymous (logged in)

	/**
	 * Returns the user id of the currently logged-in oauth user
	 *
	 * @return user id or empty string
	 */
	public static String getLoggedInUserId() {
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			return oAuth2User.getAttribute("id");
		}
		return "";
	}

	public static String buildGuildAuthentication(String roleName, long guildId) {
		return roleName + "_" + guildId;
	}

	public static String buildGuildAuthenticationWithPrefix(String roleName, long guildId) {
		return ROLE_PREFIX + buildGuildAuthentication(roleName, guildId);
	}

	public static String buildGuildAuthenticationWithPrefix(String roleName, @NonNull Guild guild) {
		return buildGuildAuthenticationWithPrefix(roleName, guild.getId());
	}

	/**
	 * @throws ForbiddenException if userId doesn't match logged in user
	 */
	public static void assertIsLoggedInUser(String userId) {
		if (!isLoggedInUser(userId)) {
			throw new ForbiddenException("Das darfst du nur für dich selbst.");
		}
	}

	/**
	 * Checks if the given userId matches the currently logged-in user
	 *
	 * @param userId to check
	 * @return true if the logged in person has the given user id
	 */
	public static boolean isLoggedInUser(@NonNull String userId) {
		return userId.equals(getLoggedInUserId());
	}

	/**
	 * Checks if the currently logged-in user has the given permission in the given guild.
	 * If the guild is the {@link de.webalf.slotbot.model.Guild#GUILD_PLACEHOLDER} the "potential permission" is checked
	 *
	 * @param role    to check
	 * @param guildId in which the permission should be present
	 * @return true if allowed
	 */
	static boolean hasPermissionInGuild(@NonNull ApplicationPermissionHelper.Role role, long guildId) {
		final Stream<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream();
		final Set<String> authorizedRoles = role.getAuthorizedRoles().stream().map(authorizedRole -> ROLE_PREFIX + authorizedRole.getApplicationRole()).collect(Collectors.toUnmodifiableSet());
		if (guildId == GUILD_PLACEHOLDER) {
			return authorities.anyMatch(grantedAuthority -> {
				final String authority = grantedAuthority.getAuthority();
				return authorizedRoles.stream().anyMatch(authority::equals);
			});
		} else {
			final String guild = Long.toString(guildId);
			return authorities.anyMatch(grantedAuthority -> {
				final String authority = grantedAuthority.getAuthority();
				return authority.endsWith(guild) && authorizedRoles.stream().anyMatch(authority::startsWith);
			});
		}
	}

	public static boolean hasEventManagePermission(Long guildId) {
		return hasPermissionInGuild(EVENT_MANAGE, guildId);
	}
}
