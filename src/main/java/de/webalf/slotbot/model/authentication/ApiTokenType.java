package de.webalf.slotbot.model.authentication;

import lombok.Value;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;

/**
 * @author Alf
 * @since 06.03.2021
 */
public enum ApiTokenType {
	ADMIN,       //Administrative endpoints
	WRITE,       //Can write all data
	READ,        //Can read all data
	READ_PUBLIC; //Can read all public available data

	public Set<ApiTokenType> getAuthorizedTokenTypes() {
		final ApiTokenType[] apiTokenTypes = ApiTokenType.values();
		final int i = Arrays.asList(apiTokenTypes).indexOf(this);
		return Arrays.stream(apiTokenTypes).limit(i + 1).collect(Collectors.toUnmodifiableSet());
	}

	@Value
	public static class TypeRoleNames { //These static strings are needed for the PreAuthorize annotation. They must be synchronous to the enums above.
		public static final String READ_PUBLIC = ROLE_PREFIX + "READ_PUBLIC";
		public static final String READ = ROLE_PREFIX + "READ";
		public static final String WRITE = ROLE_PREFIX + "WRITE";
		public static final String ADMIN = ROLE_PREFIX + "ADMIN";
	}
}
