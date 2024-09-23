package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 07.01.2022
 */
@Service
@RequiredArgsConstructor
public class PermissionChecker {
	private final GuildService guildService;
	private final EventService eventService;

	public boolean hasEventManagePermissionInCurrentGuild() {
		return PermissionHelper.hasEventManagePermission(guildService.getCurrentGuildId());
	}

	public boolean hasEventManagePermission(long eventId) {
		return PermissionHelper.hasEventManagePermission(eventService.getGuildByEventId(eventId).getId());
	}

	public boolean hasAdminPermissionInCurrentGuild() {
		return PermissionHelper.hasAdministratorPermission(guildService.getCurrentGuildId());
	}

	public boolean hasAdminPermission(long guildId) {
		return PermissionHelper.hasAdministratorPermission(guildId);
	}

	public boolean hasAdminPermissionForEvent(long eventId) {
		return hasAdminPermission(eventService.getGuildByEventId(eventId).getId());
	}
}
