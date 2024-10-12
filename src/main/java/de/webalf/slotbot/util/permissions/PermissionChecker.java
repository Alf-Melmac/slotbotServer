package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Alf
 * @since 07.01.2022
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused") //Obviously not, every method is used in SpEL
public class PermissionChecker {
	private final GuildService guildService;
	private final EventService eventService;

	public boolean isAdvancedGuild(String identifier) {
		return guildService.isAdvanced(identifier);
	}

	public boolean hasEventManagePermission(long guildId) {
		return PermissionHelper.hasEventManagePermission(guildId);
	}

	public boolean hasEventManagePermissionIn(Optional<String> identifier) {
		return PermissionHelper.hasEventManagePermission(guildService.getIdByIdentifier(identifier));
	}

	public boolean hasEventManagePermissionForEvent(long eventId) {
		return PermissionHelper.hasEventManagePermission(eventService.getGuildByEventId(eventId).getId());
	}

	public boolean hasAdminPermission(long guildId) {
		return PermissionHelper.hasAdministratorPermission(guildId);
	}

	public boolean hasAdminPermissionIn(String identifier) {
		return PermissionHelper.hasAdministratorPermission(guildService.getIdByIdentifier(identifier));
	}

	public boolean hasAdminPermissionForEvent(long eventId) {
		return hasAdminPermission(eventService.getGuildByEventId(eventId).getId());
	}
}
