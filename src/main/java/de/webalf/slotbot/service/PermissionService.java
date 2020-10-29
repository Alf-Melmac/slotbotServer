package de.webalf.slotbot.service;

import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Value
public class PermissionService {
	static String ROLE_PREFIX = "ROLE_";

	public static final String ADMIN = "ADMIN";
	public static final String MOD = "MOD";
	public static final String CREATOR = "CREATOR";
	public static final String GAMER = "GAMER";

	/**
	 * Roles that are allowed to manage events
	 *
	 * @return set of allowed role names
	 */
	public static String[] getEventManageRoles() {
		List<String> administrativeRoles = new ArrayList<>(Arrays.asList(getAdministrativeRoles()));
		administrativeRoles.add(ROLE_PREFIX + CREATOR);
		return administrativeRoles.toArray(String[]::new);
	}

	/**
	 * Roles tht are allowed to performe administrative functions
	 *
	 * @return set of allowed role names
	 */
	private static String[] getAdministrativeRoles() {
		return Arrays.asList(ROLE_PREFIX + ADMIN,
				ROLE_PREFIX + MOD).toArray(String[]::new);
	}
}
