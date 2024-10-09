package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.configuration.authentication.api.SlotbotAuthentication;
import de.webalf.slotbot.model.authentication.ApiTokenType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.*;
import static de.webalf.slotbot.model.authentication.ApiTokenType.TypeRoleNames.*;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class ApiPermissionHelper {
	public static final String HAS_POTENTIAL_READ_PUBLIC_PERMISSION = HAS_ANY_ROLE + READ_PUBLIC + HAS_ANY_ROLE_NEXT + READ + HAS_ANY_ROLE_NEXT + WRITE + HAS_ANY_ROLE_NEXT + ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_POTENTIAL_READ_PERMISSION = HAS_ANY_ROLE + READ + HAS_ANY_ROLE_NEXT + WRITE + HAS_ANY_ROLE_NEXT + ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_POTENTIAL_WRITE_PERMISSION = HAS_ANY_ROLE + WRITE + HAS_ANY_ROLE_NEXT + ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_ADMIN_PERMISSION = HAS_ROLE + ADMIN + HAS_ROLE_CLOSE;

	/**
	 * Checks if the currently logged-in is allowed to read in the given guild
	 *
	 * @param publicItem switch to identify if {@link ApiTokenType#READ_PUBLIC} is sufficient
	 * @param guild      to check read permission for
	 * @return true if permission is given
	 */
	public static boolean hasReadPermission(boolean publicItem, long guild) {
		return publicItem ? hasPermissionForGuild(ApiTokenType.READ_PUBLIC, guild) : hasPermissionForGuild(ApiTokenType.READ, guild);
	}

	/**
	 * Checks if the currently logged-in is allowed to write in the given guild
	 *
	 * @param guildId guild to check write permission for
	 * @return true if permission is given
	 */
	public static boolean hasWritePermission(long guildId) {
		return hasPermissionForGuild(ApiTokenType.WRITE, guildId);
	}

	/**
	 * Checks if the currently logged-in token has the given needed permission for the given guild
	 *
	 * @param apiTokenType Permission level required
	 * @param guild        guild to check permission for
	 * @return true if permission is given
	 */
	private static boolean hasPermissionForGuild(@NonNull ApiTokenType apiTokenType, long guild) {
		if (ApiTokenType.READ_PUBLIC == apiTokenType) { //Every token is allowed to read public data
			return true;
		}

		final Set<String> authorizedRoles = apiTokenType.getAuthorizedTokenTypes().stream()
				.map(tokenType -> PermissionHelper.buildGuildAuthenticationWithPrefix(tokenType.name(), guild))
				.collect(Collectors.toUnmodifiableSet());
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(grantedAuthority -> authorizedRoles.contains(grantedAuthority.getAuthority()));
	}

	/**
	 * @param guild to check
	 * @return true if currently logged-in token guild is identical to the given guild
	 */
	public static boolean isCurrentGuild(long guild) {
		return guild == getTokenGuild();
	}

	/**
	 * Fetches the guildId of the active token via {@link SlotbotAuthentication#getDetails()}
	 *
	 * @return current guild id
	 */
	public static long getTokenGuild() {
		return (long) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
