package de.webalf.slotbot.util.permissions;

import lombok.experimental.UtilityClass;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.*;

/**
 * @author Alf
 * @since 26.09.2023
 */
@UtilityClass
public class ApplicationRole {
	static final String SYS_ADMIN = "SYS_ADMIN";
	static final String ADMIN = "ADMIN";
	static final String EVENT_MANAGE = "EVENT_MANAGE";

	public static final String HAS_ROLE_SYS_ADMIN = HAS_ROLE + ROLE_PREFIX + SYS_ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_POTENTIALLY_ROLE_ADMIN = HAS_ROLE + ROLE_PREFIX + ADMIN + HAS_ROLE_CLOSE;
	public static final String HAS_POTENTIALLY_ROLE_EVENT_MANAGE = HAS_ANY_ROLE + ADMIN + HAS_ANY_ROLE_NEXT + ROLE_PREFIX + EVENT_MANAGE + HAS_ROLE_CLOSE;
	public static final String HAS_ROLE_EVERYONE = IS_AUTHENTICATED;

}
