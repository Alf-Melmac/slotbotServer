package de.webalf.slotbot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.service.external.DiscordApiService.*;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.PermissionHelper.Role.*;
import static de.webalf.slotbot.util.bot.MessageUtils.getKnownRoles;

/**
 * @author Alf
 * @since 29.10.2020
 */
@UtilityClass
public final class PermissionHelper {
	private static final String ROLE_PREFIX = "ROLE_";

	private static final String HAS_ROLE = "hasRole('";
	private static final String HAS_ANY_ROLE = "hasAnyRole('";
	private static final String HAS_ANY_ROLE_NEXT = "', '";
	private static final String HAS_ROLE_CLOSE = "')";

	public static final String ADMINISTRATIVE_ROLES = ROLE_PREFIX + ApplicationRoles.SYS_ADMIN + HAS_ANY_ROLE_NEXT + ROLE_PREFIX + PermissionHelper.Role.ApplicationRoles.ADMIN + HAS_ANY_ROLE_NEXT + PermissionHelper.Role.ApplicationRoles.MOD;
	public static final String HAS_ROLE_ADMIN = HAS_ANY_ROLE + ADMINISTRATIVE_ROLES + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_CREATOR = HAS_ANY_ROLE + ADMINISTRATIVE_ROLES + HAS_ANY_ROLE_NEXT + ROLE_PREFIX + PermissionHelper.Role.ApplicationRoles.CREATOR + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_ARMA = HAS_ROLE + ROLE_PREFIX + ApplicationRoles.ARMA + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_EVERYONE = "isAuthenticated()"; //Isn't anonymous (logged in)

	@Getter
	@AllArgsConstructor
	public enum Role {
		SERVER_ADMIN(ROLE_SERVER_ADMIN, ApplicationRoles.SYS_ADMIN),
		ADMINISTRATOR(ROLE_ADMINISTRATOR, ApplicationRoles.ADMIN),
		MODERATOR(ROLE_MODERATOR, ApplicationRoles.MOD),
		CREATOR(ROLE_CREATOR, ApplicationRoles.CREATOR),
		ARMA(ROLE_ARMA, ApplicationRoles.ARMA),
		EVERYONE(ROLE_EVERYONE, ApplicationRoles.USER);

		@NotBlank
		private final String discordRole;
		@NotBlank
		private final String applicationRole;

		public static final class ApplicationRoles {
			public static final String SYS_ADMIN = "SYS_ADMIN";
			public static final String ADMIN = "ADMIN";
			public static final String MOD = "MOD";
			public static final String CREATOR = "CREATOR";
			public static final String ARMA = "ARMA";
			public static final String USER = "USER";
		}

		private static final Map<String, Role> DISCORD_ROLE_VALUES;
		private static final Map<String, Role> APPLICATION_ROLE_VALUES;

		static {
			Map<String, Role> discordRoleMap = new HashMap<>();
			Map<String, Role> applicationRoleMap = new HashMap<>();
			for (Role role : EnumSet.allOf(Role.class)) {
				discordRoleMap.put(role.getDiscordRole(), role);
				applicationRoleMap.put(role.getApplicationRole(), role);
			}
			DISCORD_ROLE_VALUES = Collections.unmodifiableMap(discordRoleMap);
			APPLICATION_ROLE_VALUES = Collections.unmodifiableMap(applicationRoleMap);
		}

		/**
		 * Returns the {@link Role} matching the given discord role
		 *
		 * @param role to search for
		 * @return the matching role or null if not found
		 */
		public static Role getByDiscordRole(String role) {
			return DISCORD_ROLE_VALUES.get(role);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum Authorization {
		ADMINISTRATIVE(new Role[]{SERVER_ADMIN, ADMINISTRATOR, MODERATOR}),
		EVENT_MANAGE(Stream.concat(Arrays.stream(ADMINISTRATIVE.getRoles()), Stream.of(Role.CREATOR)).toArray(Role[]::new)),
		SLOT(new Role[]{Role.ARMA}),
		NONE(new Role[]{EVERYONE});

		@NonNull
		private final Role[] roles;
	}

	/**
	 * Application role names with {@link #ROLE_PREFIX} that are allowed to manage events
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
	private static Set<String> getApplicationRoles(Role[] roles) {
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
