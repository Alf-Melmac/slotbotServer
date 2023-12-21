package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.event.EventArchiveEvent;
import de.webalf.slotbot.model.event.EventArchiveInitializedEvent;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.bot.BotService;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.ChannelUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;

/**
 * @author Alf
 * @since 23.07.2023
 */
@Service
@RequiredArgsConstructor
public class EventArchiveBotListener {
	private final GuildService guildService;
	private final BotService botService;

	@EventListener
	public void onEventArchiveEvent(@NonNull EventArchiveEvent event) {
		onEventArchive(event.event(), guildService.find(event.guildId()), botService.getJda().getGuildById(event.guildId()));
	}

	@EventListener
	public void onEventArchiveEvent(@NonNull EventArchiveInitializedEvent event) {
		onEventArchive(event.event(), event.guild(), event.discordGuild());
	}

	private static void onEventArchive(@NotNull Event event, @NonNull de.webalf.slotbot.model.Guild guild, Guild discordGuild) {
		final Long archiveChannelId = guild.getArchiveChannel();
		if (archiveChannelId != null) {
			final MessageChannel archiveChannel = ChannelUtils.getChannel(archiveChannelId, discordGuild, "archive");
			if (archiveChannel == null) {
				return;
			}
			sendMessage(archiveChannel, EventUtils.buildArchiveMessage(event));
		}
	}
}
