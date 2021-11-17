package de.webalf.slotbot.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.GUILD;
import static de.webalf.slotbot.constant.AuthorizationCheckValues.ROLE_PREFIX;
import static de.webalf.slotbot.model.authentication.ApiTokenType.TypeRoleNames.READ;
import static de.webalf.slotbot.model.authentication.ApiTokenType.TypeRoleNames.READ_PUBLIC;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alf
 * @since 13.10.2021
 */
@SpringBootTest
@Disabled //FIXME Enable git to run this IT-Test
class EventUtilsTest {
	private static final long OWN_GUILD = 123;
	private static final long FOREIGN_GUILD = 456;
	private final String GUILD_ROLE = ROLE_PREFIX + GUILD + "_" + OWN_GUILD;
	private final String READ_PUBLIC_GUILD = READ_PUBLIC + "_" + OWN_GUILD;
	private final String READ_GUILD = READ + "_" + OWN_GUILD;
	private final String READ_PUBLIC_ANY = READ_PUBLIC;
	private final String READ_ANY = READ;

	//Own event
	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadPublicIfOwnEventNotHidden() {
		assertTrue(EventUtils.apiReadAccessAllowed(false, false, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadPublicIfOwnEventHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(false, true, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_ANY, READ_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadIfOwnEventHidden() {
		assertTrue(EventUtils.apiReadAccessAllowed(false, true, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadPublicIfOwnEventNotHiddenAndShareable() {
		assertTrue(EventUtils.apiReadAccessAllowed(true, false, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadPublicIfOwnEventHiddenAndShareable() {
		assertFalse(EventUtils.apiReadAccessAllowed(true, true, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_ANY, READ_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadIfOwnEventHiddenAndShareable() {
		assertTrue(EventUtils.apiReadAccessAllowed(true, true, OWN_GUILD));
	}

	//Foreign event
	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadPublicIfForeignEventNotShareableAndNotHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(false, false, FOREIGN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_ANY, READ_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadIfForeignEventNotShareableAndNotHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(false, false, FOREIGN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadPublicIfForeignEventNotShareableAndHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(false, true, FOREIGN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_ANY, READ_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadIfForeignEventNotShareableAndHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(false, true, FOREIGN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadPublicIfForeignEventShareableAndNotHidden() {
		assertTrue(EventUtils.apiReadAccessAllowed(true, false, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_PUBLIC_ANY, READ_PUBLIC_GUILD, GUILD_ROLE})
	void apiReadAccessForbiddenWithReadPublicIfForeignEventShareableAndHidden() {
		assertFalse(EventUtils.apiReadAccessAllowed(true, true, OWN_GUILD));
	}

	@Test
	@WithMockUser(authorities = {READ_ANY, READ_GUILD, GUILD_ROLE})
	void apiReadAccessAllowedWithReadIfForeignEventShareableAndHidden() {
		assertTrue(EventUtils.apiReadAccessAllowed(true, true, OWN_GUILD));
	}
}