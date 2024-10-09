package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.enums.DiscordUserObjectFields;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.util.DiscordOAuthUtils.getAttribute;
import static de.webalf.slotbot.util.permissions.Role.*;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class PermissionHelper {
	/**
	 * Returns the user id of the currently logged-in oauth user
	 *
	 * @return user id or empty string
	 */
	public static String getLoggedInUserId() {
		final OAuth2User user = getLoggedIn();
		if (user == null) {
			return "";
		}
		return getAttribute(user, DiscordUserObjectFields.ID);
	}

	public static Set<String> getAuthoritiesOfLoggedInUser() {
		OAuth2User user = getLoggedIn();
		if (user == null) {
			return Collections.emptySet();
		}
		return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(APPLICATION_ROLE_VALUES.keySet()::contains).collect(Collectors.toSet());
	}

	public static OAuth2User getLoggedIn() {
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			return oAuth2User;
		}
		return null;
	}

	public static String buildAuthenticationWithPrefix(@NotBlank String roleName) {
		return ROLE_PREFIX + roleName;
	}

	public static String buildGuildAuthenticationWithPrefix(@NotBlank String roleName, long guildId) {
		return buildAuthenticationWithPrefix(roleName + "_" + guildId);
	}

	public static String buildGuildAuthenticationWithPrefix(String roleName, @NonNull Guild guild) {
		return buildGuildAuthenticationWithPrefix(roleName, guild.getId());
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
	 * @see #isLoggedInUser(String)
	 */
	public static boolean isLoggedInUser(long userId) {
		return isLoggedInUser(Long.toString(userId));
	}

	/**
	 * Checks if the currently logged-in user has the given permission in the given guild.
	 *
	 * @param role    to check
	 * @param guildId in which the permission should be present
	 * @return true if allowed
	 */
	public static boolean hasPermissionInGuild(@NonNull Role role, long guildId) {
		final Stream<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream();
		final Set<String> authorizedRoles = role.getAuthorizedRoles().stream().map(authorizedRole -> ROLE_PREFIX + authorizedRole.getApplicationRole()).collect(Collectors.toUnmodifiableSet());
		if (role == SYSTEM_ADMIN) {
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
	 * Checks for the {@link Role#ADMINISTRATOR} role
	 *
	 * @see #hasPermissionInGuild(Role, long)
	 */
	public static boolean hasAdministratorPermission(long guildId) {
		return hasPermissionInGuild(ADMINISTRATOR, guildId);
	}

	/**
	 * Checks for the {@link Role#EVENT_MANAGE} role
	 *
	 * @see #hasPermissionInGuild(Role, long)
	 */
	public static boolean hasEventManagePermission(long guildId) {
		return hasPermissionInGuild(EVENT_MANAGE, guildId);
	}
}
