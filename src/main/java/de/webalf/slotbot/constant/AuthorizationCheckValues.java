package de.webalf.slotbot.constant;

import lombok.Value;

/**
 * @author Alf
 * @since 06.03.2021
 */
@Value
public class AuthorizationCheckValues {
	public static final String ROLE_PREFIX = "ROLE_";

	public static final String HAS_ROLE = "hasRole('";
	public static final String HAS_ANY_ROLE = "hasAnyRole('";
	public static final String HAS_ANY_ROLE_NEXT = "', '";
	public static final String HAS_ROLE_CLOSE = "')";
}
