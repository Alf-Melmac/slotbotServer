package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Wrapper for {@link GuildService} to be used by discord bot
 *
 * @author Alf
 * @since 15.02.2023
 */
@Service
@RequiredArgsConstructor
public class GuildBotService {
	private final GuildService guildService;

	public Guild find(long discordId) {
		return guildService.findExistingByDiscordId(discordId);
	}

	public Locale getGuildLocale(long discordId) {
		return find(discordId).getLocale();
	}

	public Long getGuildArchiveChannel(long discordId) {
		return find(discordId).getArchiveChannel();
	}

	public void removeArchiveChannelByChannel(long discordId, long removedChannelId) {
		guildService.removeArchiveChannelByChannel(discordId, removedChannelId);
	}
}
