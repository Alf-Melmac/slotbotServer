package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.util.permissions.Role;
import lombok.Builder;
import lombok.Value;

/**
 * {@link DiscordUserDto Discord user} in the specific context of a guild
 *
 * @author Alf
 * @since 18.01.2023
 */
@Value
@Builder
public class UserInGuildDto {
	DiscordUserDto user;
	Role role;
}
