package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.constant.AuthorizationCheckValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.util.bot.MessageUtils.getKnownRoles;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.Role.*;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class BotPermissionHelper {
	@Getter
	@AllArgsConstructor
	public enum Authorization {
		ADMINISTRATIVE(new ApplicationPermissionHelper.Role[]{SERVER_ADMIN, ADMINISTRATOR, MODERATOR}),
		EVENT_MANAGE(Stream.concat(Arrays.stream(ADMINISTRATIVE.getRoles()), Stream.of(ApplicationPermissionHelper.Role.CREATOR)).toArray(ApplicationPermissionHelper.Role[]::new)),
		SLOT(new ApplicationPermissionHelper.Role[]{ApplicationPermissionHelper.Role.ARMA}),
		NONE(new ApplicationPermissionHelper.Role[]{EVERYONE});

		@NonNull
		private final ApplicationPermissionHelper.Role[] roles;
	}

	/**
	 * Application role names with {@link AuthorizationCheckValues#ROLE_PREFIX} that are allowed to manage events
	 *
	 * @return set of allowed role names
	 */
	public static Set<String> getEventManageApplicationRoles() {
		return getApplicationRoles(EVENT_MANAGE.getRoles());
	}

	/**
	 * Returns the application role names for the given roles
	 *
	 * @param roles to get the names for
	 * @return application role names
	 */
	private static Set<String> getApplicationRoles(ApplicationPermissionHelper.Role[] roles) {
		return Arrays.stream(roles).map(role -> ROLE_PREFIX + role.getApplicationRole()).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Checks if the message author has any of the authorized roles
	 *
	 * @param authorization Needed authorization
	 * @param message       of the person that needs to be checked
	 * @return true if authorized
	 */
	public static boolean isAuthorized(@NonNull Authorization authorization, Message message) {
		return Arrays.stream(authorization.getRoles()).anyMatch(role -> getKnownRoles(message).contains(role));
	}

	/**
	 * Checks if the currently logged in person is allowed to manage events
	 *
	 * @return true if allowed
	 */
	public static boolean hasEventManageRole() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(grantedAuthority -> getEventManageApplicationRoles().contains(grantedAuthority.getAuthority()));
	}
}
