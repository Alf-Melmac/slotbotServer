package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.service.event.EventArchiveEvent;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.ChannelUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
	@EventListener
	public void onEventArchiveEvent(@NonNull EventArchiveEvent event) {
		final Guild discordGuild = event.discordGuild();
		final Long archiveChannelId = event.guild().getArchiveChannel();
		if (archiveChannelId != null) {
			final TextChannel archiveChannel = ChannelUtils.getChannel(archiveChannelId, discordGuild, "archive");
			if (archiveChannel == null) {
				return;
			}
			sendMessage(archiveChannel, EventUtils.buildArchiveMessage(event.event()));
		}
	}
}
