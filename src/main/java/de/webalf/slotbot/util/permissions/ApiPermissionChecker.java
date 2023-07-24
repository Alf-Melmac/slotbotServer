package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.hasWritePermission;

/**
 * @author Alf
 * @since 07.01.2022
 */
@Service
@RequiredArgsConstructor
public class ApiPermissionChecker {
	private final GuildService guildService;

	/**
	 * Checks if write permission is given for the {@link GuildService#findCurrentNonNullGuild() current guild}
	 */
	public boolean assertApiWriteAccess() {
		assertApiWriteAccessAllowed(guildService.findCurrentNonNullGuild());
		return true;
	}

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 */
	public static void assertApiWriteAccess(@NonNull Event event) {
		assertApiWriteAccessAllowed(event.getOwnerGuild());
	}

	/**
	 * Checks if write permission is given for the given event.
	 *
	 * @param ownerGuild event owner guild
	 * @throws ForbiddenException if write permission is not given
	 */
	private static void assertApiWriteAccessAllowed(Guild ownerGuild) throws ForbiddenException {
		if (!hasWritePermission(ownerGuild)) {
			throw new ForbiddenException("Not allowed to write here.");
		}
	}
}
