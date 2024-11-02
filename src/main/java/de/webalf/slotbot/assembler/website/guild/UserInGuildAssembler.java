package de.webalf.slotbot.assembler.website.guild;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.dtos.website.guild.UserInGuildDto;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.service.external.DiscordBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Component
@RequiredArgsConstructor
public class UserInGuildAssembler {
	private final DiscordBotService discordBotService;

	public UserInGuildDto toDto(@NonNull GuildUser guildUser, @NonNull Guild guild) {
		final DiscordGuildMember member = discordBotService.getGuildMember(guildUser.getId().getUserId(), guild.getId());
		if (member == null) return null;

		return UserInGuildDto.builder()
				.user(DiscordUserAssembler.toDto(member))
				.role(guildUser.getRole())
				.build();
	}
}
