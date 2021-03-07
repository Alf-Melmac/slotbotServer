package de.webalf.slotbot.model.authentication;

import lombok.Value;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;

/**
 * @author Alf
 * @since 06.03.2021
 */
public enum ApiTokenType {
	READ_PUBLIC, //Can read all public available data
	READ,        //Can read all data
	WRITE;       //Can write all data

	@Value
	public static class TypeRoleNames { //These static strings are needed for the PreAuthorize annotation. They must be synchronous to the enums above.
		public static final String READ_PUBLIC = ROLE_PREFIX + "READ_PUBLIC";
		public static final String READ = ROLE_PREFIX + "READ";
		public static final String WRITE = ROLE_PREFIX + "WRITE";
	}
}
