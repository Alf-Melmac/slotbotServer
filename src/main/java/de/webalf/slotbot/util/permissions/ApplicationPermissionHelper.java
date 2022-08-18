package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.*;
import static de.webalf.slotbot.service.external.DiscordAuthenticationService.*;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.Role.*;
import static de.webalf.slotbot.util.permissions.PermissionHelper.IS_AUTHENTICATED;

/**
 * @author Alf
 * @since 29.10.2020
 */
@UtilityClass
public final class ApplicationPermissionHelper {
	private static final String ADMINISTRATIVE_ROLES = ROLE_PREFIX + ApplicationRoles.SYS_ADMIN + HAS_ANY_ROLE_NEXT + ROLE_PREFIX + ApplicationPermissionHelper.Role.ApplicationRoles.ADMIN;
	public static final String HAS_ROLE_SYS_ADMIN = HAS_ROLE + ROLE_PREFIX + ApplicationRoles.SYS_ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_ADMIN = HAS_ANY_ROLE + ADMINISTRATIVE_ROLES + HAS_ROLE_CLOSE;
	public static final String HAS_POTENTIALLY_ROLE_EVENT_MANAGE = HAS_ANY_ROLE + ADMINISTRATIVE_ROLES + HAS_ANY_ROLE_NEXT + ROLE_PREFIX + ApplicationPermissionHelper.Role.ApplicationRoles.EVENT_MANAGE + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_EVERYONE = IS_AUTHENTICATED;

	@Getter
	@AllArgsConstructor
	public enum Role {
		SYSTEM_ADMIN(null, ApplicationRoles.SYS_ADMIN),
		ADMINISTRATOR(ROLE_ADMIN, ApplicationRoles.ADMIN),
		EVENT_MANAGE(ROLE_EVENT_MANGE, ApplicationRoles.EVENT_MANAGE),
		EVERYONE(ROLE_EVERYONE, ApplicationRoles.USER);

		private final String discordRole; //If blank, then it is a GlobalRole
		@NotBlank
		private final String applicationRole;

		@Value
		static class ApplicationRoles {
			private static final String SYS_ADMIN = "SYS_ADMIN";
			private static final String ADMIN = "ADMIN";
			private static final String EVENT_MANAGE = "EVENT_MANAGE";
			private static final String USER = "USER";
		}

		private static final Map<String, Role> DISCORD_ROLE_VALUES;
		public static final Map<String, Role> APPLICATION_ROLE_VALUES;

		static {
			final Map<String, Role> discordRoleMap = new HashMap<>();
			final Map<String, Role> applicationRoleMap = new HashMap<>();
			for (Role role : EnumSet.allOf(Role.class)) {
				final String currentDiscordRole = role.getDiscordRole();
				if (StringUtils.isNotEmpty(currentDiscordRole)) {
					discordRoleMap.put(currentDiscordRole, role);
				}
				applicationRoleMap.put(ROLE_PREFIX + role.getApplicationRole(), role);

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

		/**
		 * Returns the {@link Role} matching the given application role
		 *
		 * @param role to search for
		 * @return the matching role or null if not found
		 */
		public static Role getByApplicationRole(String role) {
			return APPLICATION_ROLE_VALUES.get(role);
		}

		public Set<Role> getAuthorizedRoles() {
			final Role[] roles = Role.values();
			final int i = Arrays.asList(roles).indexOf(this);
			return Arrays.stream(roles).limit(i + 1).collect(Collectors.toUnmodifiableSet());
		}
	}

	public static String getApplicationRoleName(String discordRole) {
		final ApplicationPermissionHelper.Role roleEnum = getByDiscordRole(discordRole);
		final String roleName = roleEnum != null ? roleEnum.getApplicationRole() : EVERYONE.getApplicationRole();
		return ROLE_PREFIX + roleName;
	}
}
