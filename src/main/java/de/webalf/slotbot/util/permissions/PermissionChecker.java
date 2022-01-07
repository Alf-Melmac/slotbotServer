package de.webalf.slotbot.util.permissions;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.bot.MentionUtils.isSnowflake;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.Role.EVENT_MANAGE;
import static de.webalf.slotbot.util.permissions.PermissionHelper.hasPermissionInGuild;

/**
 * @author Alf
 * @since 07.01.2022
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PermissionChecker {
	private final GuildService guildService;

	public boolean hasEventManagePermissionInCurrentOwnerGuild() {
		return PermissionHelper.hasEventManagePermission(guildService.getCurrentGuildId());
	}

	public void assertEventManagePermission(@NonNull Guild guild) {
		assertEventManagePermission(guild.getId());
	}

	public void assertEventManagePermission(String guildId) {
		assertEventManagePermission(LongUtils.parseLongWrapper(guildId));
	}

	public void assertEventManagePermissionInCurrentOwnerGuild() {
		assertEventManagePermission(guildService.getCurrentGuildId());
	}

	private void assertEventManagePermission(Long guildId) {
		assertPermissionInGuild(EVENT_MANAGE, guildId);
	}

	/**
	 * Asserts that the currently logged-in user has the given permission
	 *
	 * @param role    to check
	 * @param guildId in which the permission should be present
	 * @throws ForbiddenException if the permission is not present
	 * @see PermissionHelper#hasPermissionInGuild(ApplicationPermissionHelper.Role, long)
	 */
	private void assertPermissionInGuild(ApplicationPermissionHelper.Role role, Long guildId) {
		if (guildId == null || guildId == GUILD_PLACEHOLDER) {
			if (!hasPermissionInGuild(role, guildService.getCurrentGuildId())) {
				throw new ForbiddenException("Das darfst du hier nicht.");
			}
		} else if (!isSnowflake(Long.toString(guildId)) || !hasPermissionInGuild(role, guildId)) {
			throw new ForbiddenException("Das darfst du hier nicht.");
		}
	}
}
