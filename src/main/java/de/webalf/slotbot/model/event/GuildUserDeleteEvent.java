package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.util.permissions.Role;
import lombok.NonNull;

/**
 * Notifies about user removed from a guild
 *
 * @author Alf
 * @since 08.10.2023
 */
public record GuildUserDeleteEvent(long guildId, long userId, Role role) {
	public GuildUserDeleteEvent(@NonNull GuildUser guildUser) {
		this(guildUser.getGuild().getId(), guildUser.getUser().getId(), guildUser.getRole());
	}
}
