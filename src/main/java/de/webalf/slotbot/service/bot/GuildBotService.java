package de.webalf.slotbot.service.bot;

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

	public Locale getGuildLocale(long guildId) {
		return guildService.find(guildId).getLocale();
	}

	public Long getGuildArchiveChannel(long guildId) {
		return guildService.find(guildId).getArchiveChannel();
	}

	public void removeArchiveChannelByChannel(long guildId, long removedChannelId) {
		guildService.removeArchiveChannelByChannel(guildId, removedChannelId);
	}
}
