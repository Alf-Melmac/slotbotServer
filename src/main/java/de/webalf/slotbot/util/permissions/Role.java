package de.webalf.slotbot.util.permissions;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;

/**
 * @author Alf
 * @since 26.09.2023
 */
@Getter
@AllArgsConstructor
public enum Role {
	SYSTEM_ADMIN(ApplicationRole.SYS_ADMIN),
	ADMINISTRATOR(ApplicationRole.ADMIN),
	EVENT_MANAGE(ApplicationRole.EVENT_MANAGE);

	@NotBlank
	private final String applicationRole;

	public static final Map<String, Role> APPLICATION_ROLE_VALUES;

	static {
		final Map<String, Role> applicationRoleMap = new HashMap<>();
		for (Role role : EnumSet.allOf(Role.class)) {
			applicationRoleMap.put(ROLE_PREFIX + role.getApplicationRole(), role);
		}
		APPLICATION_ROLE_VALUES = Collections.unmodifiableMap(applicationRoleMap);
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
		return Arrays.stream(roles).limit(i + 1L).collect(Collectors.toUnmodifiableSet());
	}
}
