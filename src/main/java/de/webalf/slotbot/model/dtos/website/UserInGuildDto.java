package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Value;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Value
@Builder
public class UserInGuildDto {
	DiscordUserDto user;
}
