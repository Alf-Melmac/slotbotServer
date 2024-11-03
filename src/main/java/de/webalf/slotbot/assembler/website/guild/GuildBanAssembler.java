package de.webalf.slotbot.assembler.website.guild;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.model.Ban;
import de.webalf.slotbot.model.dtos.website.guild.GuildBanDto;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.service.external.DiscordBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 02.11.2024
 */
@Component
@RequiredArgsConstructor
public class GuildBanAssembler {
	private final DiscordBotService discordBotService;

	private GuildBanDto toDto(@NonNull Ban ban) {
		final long banUserId = ban.getUser().getId();
		DiscordUser user = discordBotService.getUser(banUserId);
		if (user == null) {
			user = DiscordUser.builder()
					.id(banUserId)
					.global_name(Long.toString(banUserId))
					.build();
		}

		return GuildBanDto.builder()
				.user(DiscordUserAssembler.toDto(user))
				.reason(ban.getReason())
				.bannedAt(ban.getTimestamp())
				.build();
	}

	public List<GuildBanDto> toDtoList(Iterable<? extends Ban> bans) {
		return StreamSupport.stream(bans.spliterator(), false)
				.map(this::toDto)
				.toList();
	}
}
