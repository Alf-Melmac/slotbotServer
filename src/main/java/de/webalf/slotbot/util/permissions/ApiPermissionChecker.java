package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.hasWritePermission;

/**
 * @author Alf
 * @since 07.01.2022
 */
@UtilityClass
public final class ApiPermissionChecker {
	/**
	 * Checks if write permission is given for the {@link ApiPermissionHelper#getTokenGuild() current guild}
	 */
	public static boolean assertApiWriteAccess() {
		assertApiWriteAccessAllowed(ApiPermissionHelper.getTokenGuild());
		return true;
	}

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 */
	public static void assertApiWriteAccess(@NonNull Event event) {
		assertApiWriteAccessAllowed(event.getOwnerGuild().getId());
	}

	/**
	 * Checks if write permission is given in the given guild.
	 *
	 * @param ownerGuildId guild to check for permission in
	 * @throws ForbiddenException if write permission is not given
	 */
	private static void assertApiWriteAccessAllowed(long ownerGuildId) throws ForbiddenException {
		if (!hasWritePermission(ownerGuildId)) {
			throw new ForbiddenException("Not allowed to write here.");
		}
	}
}
