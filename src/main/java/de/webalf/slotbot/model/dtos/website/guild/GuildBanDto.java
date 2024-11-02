package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 02.11.2024
 */
@Value
@Builder
public class GuildBanDto {
	DiscordUserDto user;
	String reason;
	LocalDateTime bannedAt;
}
