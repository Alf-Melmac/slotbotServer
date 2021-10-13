package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.util.GuildUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.util.GuildUtils.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.bot.MentionUtils.isSnowflake;
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
	 * If the guild is the {@link GuildUtils#GUILD_PLACEHOLDER} the "potential permission" is checked
	 *
	 * @param role    to check
	 * @param guildId in which the permission should be present
	 * @return true if allowed
	 */
	private static boolean hasPermissionInGuild(@NonNull ApplicationPermissionHelper.Role role, long guildId) {
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

	/**
	 * Asserts that the currently logged-in user has the given permission
	 *
	 * @param role    to check
	 * @param guildId in which the permission should be present
	 * @throws ForbiddenException if the permission is not present
	 * @see #hasPermissionInGuild(ApplicationPermissionHelper.Role, long)
	 */
	private static void assertPermissionInGuild(ApplicationPermissionHelper.Role role, Long guildId) {
		if (guildId == null || guildId == GUILD_PLACEHOLDER) {
			if (!hasPermissionInGuild(role, GuildUtils.getCurrentOwnerGuild())) {
				throw new ForbiddenException("Das darfst du hier nicht.");
			}
		} else if (!isSnowflake(Long.toString(guildId)) || !hasPermissionInGuild(role, guildId)) {
			throw new ForbiddenException("Das darfst du hier nicht.");
		}
	}

	public static boolean hasEventManagePermission(Long guildId) {
		return hasPermissionInGuild(EVENT_MANAGE, guildId);
	}

	public static void assertEventManagePermission(Long guildId) {
		assertPermissionInGuild(EVENT_MANAGE, guildId);
	}
}
