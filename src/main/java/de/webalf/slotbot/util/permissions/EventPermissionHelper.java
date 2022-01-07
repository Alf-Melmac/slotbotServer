package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.hasWritePermission;

/**
 * @author Alf
 * @since 07.01.2022
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventPermissionHelper {
	private final GuildService guildService;

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 */
	public void assertApiWriteAccess(AbstractEventDto event) {
		assertApiWriteAccessAllowed(guildService.getOwnerGuild(event).getId());
	}

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 */
	public void assertApiWriteAccess(Event event) {
		assertApiWriteAccessAllowed(guildService.getOwnerGuild(event).getId());
	}

	/**
	 * Checks if write permission is given for the given event.
	 *
	 * @param ownerGuild event owner guild
	 * @throws ForbiddenException if write permission is not given
	 */
	private static void assertApiWriteAccessAllowed(long ownerGuild) throws ForbiddenException {
		if (!hasWritePermission(ownerGuild)) {
			throw new ForbiddenException("Not allowed to write here.");
		}
	}
}
