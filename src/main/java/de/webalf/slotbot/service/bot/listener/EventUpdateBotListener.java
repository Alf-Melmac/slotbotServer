package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.event.EventUpdateEvent;
import de.webalf.slotbot.service.bot.BotService;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.util.EventHelper;
import de.webalf.slotbot.util.ListUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;

/**
 * @author Alf
 * @since 05.08.2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventUpdateBotListener {
	private final EventBotService eventBotService;
	private final BotService botService;
	private final EventHelper eventHelper;

	@EventListener
	@Async
	public void onEventUpdate(@NonNull EventUpdateEvent updateEvent) {
		final Event event = eventBotService.findById(updateEvent.event());

		event.getDiscordInformation().forEach(discordInformation -> {
			final MessageChannel eventChannel = botService.getJda().getChannelById(MessageChannel.class, discordInformation.getChannel());
			if (eventChannel == null) {
				throw new IllegalStateException("Channel " + discordInformation.getChannel() + " couldn't be found.");
			}

			final Locale guildLocale = discordInformation.getGuild().getLocale();
			if (updateEvent.embedChanged()) {
				log.trace("Edit embed of {} in {}", event.getId(), discordInformation.getChannel());
				eventChannel.editMessageEmbedsById(discordInformation.getInfoMsg(), eventHelper.buildDetailsEmbed(event, guildLocale)).queue();
			}
			if (updateEvent.slotlistChanged()) {
				log.trace("Edit slotlist of {} in {}", event.getId(), discordInformation.getChannel());
				final List<String> slotList = eventHelper.buildSlotList(event, discordInformation.getGuild().getId(),guildLocale);
				//noinspection ConstantConditions SlotList can't be null here
				eventChannel.editMessageById(discordInformation.getSlotListMsgPartOne(), ListUtils.shift(slotList)).queue();
				eventChannel.editMessageById(discordInformation.getSlotListMsgPartTwo(), spacerCharIfEmpty(ListUtils.shift(slotList))).queue();
			}
		});
	}
}
