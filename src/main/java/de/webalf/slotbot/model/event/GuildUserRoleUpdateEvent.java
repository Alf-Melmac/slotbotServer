package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.util.permissions.Role;
import lombok.NonNull;

/**
 * Notifies about a change in a guild user's role
 *
 * @author Alf
 * @since 03.10.2023
 */
public record GuildUserRoleUpdateEvent(long guildId, long userId, Role oldRole, Role newRole) {
	public GuildUserRoleUpdateEvent(@NonNull GuildUser guildUser, Role oldRole) {
		this(guildUser.getGuild().getId(), guildUser.getUser().getId(), oldRole, guildUser.getRole());
	}
}
