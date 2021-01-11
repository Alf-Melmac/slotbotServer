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
import static de.webalf.slotbot.util.PermissionHelper.Authorization.ADMINISTRATIVE;
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

	@Getter
	@AllArgsConstructor
	public enum Role {
		ADMINISTRATOR(ROLE_ADMINISTRATOR, "ADMIN"),
		MODERATOR(ROLE_MODERATOR, "MOD"),
		CREATOR(ROLE_CREATOR, "CREATOR"),
		ARMA(ROLE_ARMA, "ARMA"),
		EVERYONE(ROLE_EVERYONE, "USER");

		@NotBlank
		private final String discordRole;
		@NotBlank
		private final String applicationRole;

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
		ADMINISTRATIVE(new Role[]{ADMINISTRATOR, MODERATOR}),
		EVENT_MANAGE(Stream.concat(Arrays.stream(ADMINISTRATIVE.getRoles()), Stream.of(Role.CREATOR)).toArray(Role[]::new)),
		SLOT(new Role[]{Role.ARMA}),
		NONE(new Role[]{EVERYONE});

		@NonNull
		private final Role[] roles;
	}

	/**
	 * Application role names that are allowed to perform administrative functions
	 *
	 * @return array of allowed role names
	 */
	public static String[] getAdministrativeRolesNames() {
		return getApplicationRolesNames(ADMINISTRATIVE.getRoles());
	}

	/**
	 * Application role names that are allowed to manage events
	 *
	 * @return array of allowed role names
	 */
	public static String[] getEventManageApplicationRolesNames() {
		return getApplicationRolesNames(EVENT_MANAGE.getRoles());
	}

	private static String[] getApplicationRolesNames(Role[] roles) {
		return Arrays.stream(roles).map(Role::getApplicationRole).toArray(String[]::new);
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
	 * @param message of the person that needs to be checked
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
