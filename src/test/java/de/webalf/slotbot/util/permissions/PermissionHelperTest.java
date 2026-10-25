package de.webalf.slotbot.util.permissions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Stream;

import static de.webalf.slotbot.util.permissions.PermissionHelper.buildAuthenticationWithPrefix;
import static de.webalf.slotbot.util.permissions.PermissionHelper.buildGuildAuthenticationWithPrefix;
import static de.webalf.slotbot.util.permissions.Role.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 25.10.2026
 */
class PermissionHelperTest {
	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource
	void hasPermissionInGuild(String name, String authority, Role role, long guildId, boolean expected) {
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", null, authority));

		assertEquals(expected, PermissionHelper.hasPermissionInGuild(role, guildId));
	}

	private static Stream<Arguments> hasPermissionInGuild() {
		final String admin = ADMINISTRATOR.getApplicationRole();
		final String eventManage = EVENT_MANAGE.getApplicationRole();
		final String systemAdmin = SYSTEM_ADMIN.getApplicationRole();

		return Stream.of(
				Arguments.of("Admin", buildGuildAuthenticationWithPrefix(admin, 1), ADMINISTRATOR, 1, true),
				Arguments.of("Admin has event manage", buildGuildAuthenticationWithPrefix(admin, 1), EVENT_MANAGE, 1, true),
				Arguments.of("Event manager in guild", buildGuildAuthenticationWithPrefix(eventManage, 1), EVENT_MANAGE, 1, true),
				Arguments.of("Event manager has no admin", buildGuildAuthenticationWithPrefix(eventManage, 1), ADMINISTRATOR, 1, false),
				Arguments.of("Admin in other guild", buildGuildAuthenticationWithPrefix(admin, 2), ADMINISTRATOR, 1, false),
				Arguments.of("Admin in other guild with id ending in guild id", buildGuildAuthenticationWithPrefix(admin, 11), ADMINISTRATOR, 1, false),
				Arguments.of("Event manager in other guild with id ending in guild id", buildGuildAuthenticationWithPrefix(eventManage, 21), EVENT_MANAGE, 1, false),
				Arguments.of("Admin in other guild with id starting with guild id", buildGuildAuthenticationWithPrefix(admin, 12), ADMINISTRATOR, 1, false),

				Arguments.of("Potential admin without guild", buildAuthenticationWithPrefix(admin), ADMINISTRATOR, 1, false),
				Arguments.of("System admin", buildAuthenticationWithPrefix(systemAdmin), SYSTEM_ADMIN, 1, true)
		);
	}
}
