package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.util.permissions.Role;
import lombok.NonNull;

/**
 * Notifies about a new guild user
 *
 * @author Alf
 * @since 08.10.2023
 */
public record GuildUserCreatedEvent(long guildId, long userId, Role role) {
	public GuildUserCreatedEvent(@NonNull GuildUser guildUser) {
		this(guildUser.getGuild().getId(), guildUser.getUser().getId(), guildUser.getRole());
	}
}
