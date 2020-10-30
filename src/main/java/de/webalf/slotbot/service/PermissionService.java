package de.webalf.slotbot.service;

import lombok.Value;

import java.util.*;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Value
public class PermissionService {
	private static final String ROLE_PREFIX = "ROLE_";

	public static final String ADMIN = "ADMIN";
	public static final String MOD = "MOD";
	public static final String CREATOR = "CREATOR";
	public static final String GAMER = "GAMER";

	/**
	 * Roles that are allowed to manage events
	 *
	 * @return set of allowed role names
	 */
	public static Set<String> getEventManageRoles() {
		Set<String> roles = new HashSet<>(getAdministrativeRoles());
		roles.add(ROLE_PREFIX + CREATOR);
		return roles;
	}

	/**
	 * Role names tht are allowed to perform administrative functions
	 *
	 * @return array of allowed role names
	 */
	private static Set<String> getAdministrativeRoles() {
		return Set.of(ROLE_PREFIX + ADMIN, ROLE_PREFIX + MOD);
	}

	/**
	 * Role names that are allowed to manage events
	 *
	 * @return array of allowed role names
	 */
	public static String[] getEventManageRoleNames() {
		List<String> administrativeRoles = new ArrayList<>(Arrays.asList(getAdministrativeRoleNames()));
		administrativeRoles.add(CREATOR);
		return administrativeRoles.toArray(String[]::new);
	}

	/**
	 * Role names tht are allowed to perform administrative functions
	 *
	 * @return array of allowed role names
	 */
	private static String[] getAdministrativeRoleNames() {
		return Arrays.asList(
				ADMIN,
				MOD)
				.toArray(String[]::new);
	}
}
