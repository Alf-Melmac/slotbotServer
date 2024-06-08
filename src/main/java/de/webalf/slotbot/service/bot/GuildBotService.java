package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.EventDiscordInformationService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Wrapper for {@link GuildService} and {@link EventDiscordInformationService} to be used by discord bot
 *
 * @author Alf
 * @since 15.02.2023
 */
@Service
@RequiredArgsConstructor
public class GuildBotService {
	private final GuildService guildService;
	private final EventDiscordInformationService eventDiscordInformationService;

	Guild find(long guildId) {
		return guildService.find(guildId);
	}

	public Locale getGuildLocale(long guildId) {
		return find(guildId).getLocale();
	}

	public Long getGuildArchiveChannel(long guildId) {
		return find(guildId).getArchiveChannel();
	}

	public void removeArchiveChannelByChannel(long guildId, long removedChannelId) {
		guildService.removeArchiveChannelByChannel(guildId, removedChannelId);
	}

	public void archiveByChannel(long guildId, long channelId) {
		eventDiscordInformationService.removeByChannel(guildId, channelId);
	}
}
