package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.EventDiscordInformationService;
import de.webalf.slotbot.service.bot.GuildBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateLockedEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static de.webalf.slotbot.util.bot.ChannelUtils.botHasPermission;
import static net.dv8tion.jda.api.Permission.MANAGE_EVENTS;

/**
 * This requires Intents
 * <ul>
 *     <li>{@link GatewayIntent#GUILD_MESSAGES} to listen for deleted messages</li>
 *     <li>{@link GatewayIntent#SCHEDULED_EVENTS} to listen for deleted scheduled events</li>
 * </ul>
 *
 * @author Alf
 * @since 03.04.2023
 */
@RequiredArgsConstructor
@Slf4j
public class GuildContentListener extends ListenerAdapter {
	private final EventDiscordInformationService eventDiscordInformationService;
	private final GuildBotService guildBotService;
	private final MessageSource messageSource;

	@Override
	public void onChannelDelete(@NonNull ChannelDeleteEvent event) {
		if (!event.isFromGuild()) return;
		final long guildId = event.getGuild().getIdLong();
		final long removedChannelId = event.getChannel().getIdLong();
		log.trace("Channel {} deleted in guild {}", removedChannelId, guildId);
		channelAccessRemoved(guildId, removedChannelId);
	}

	@Override
	public void onChannelUpdateLocked(@NonNull ChannelUpdateLockedEvent event) {
		if (!Boolean.TRUE.equals(event.getNewValue())) return;
		final long guildId = event.getGuild().getIdLong();
		final long channelId = event.getChannel().getIdLong();
		log.trace("Channel {} locked in guild {}", channelId, guildId);
		channelAccessRemoved(guildId, channelId);
	}

	private void channelAccessRemoved(long guildId, long removedChannelId) {
		eventDiscordInformationService.removeByChannel(guildId, removedChannelId);
		guildBotService.removeArchiveChannelByChannel(guildId, removedChannelId);
	}

	@Override
	public void onMessageDelete(@NonNull MessageDeleteEvent event) {
		if (!event.isFromGuild()) return;
		final GuildMessageChannel channel = event.getChannel().asGuildMessageChannel();
		log.trace("Message {} deleted in channel {} in guild {}", event.getMessageId(), channel.getId(), event.getGuild().getId());

		eventDiscordInformationService.removeByMessage(channel.getIdLong(), event.getMessageIdLong(), discordInformation -> {
			final Locale guildLocale = guildBotService.getGuildLocale(event.getGuild().getIdLong());
			channel.sendMessage(messageSource.getMessage("event.discordInformation.broken", null, guildLocale)).queue();

			final Long scheduledEventId = discordInformation.getScheduledEvent();
			if (scheduledEventId != null && botHasPermission(channel, MANAGE_EVENTS)) { //FIXME This is currently broken in JDA https://github.com/discord-jda/JDA/issues/3059
				channel.getGuild().retrieveScheduledEventById(scheduledEventId).queue(
						scheduledEvent -> scheduledEvent.delete().queue());
			}
		});
	}

	@Override
	public void onScheduledEventDelete(@NonNull ScheduledEventDeleteEvent event) {
		log.trace("Event {} deleted in guild {}", event.getScheduledEvent().getId(), event.getGuild().getId());

		eventDiscordInformationService.removeScheduledEvent(event.getScheduledEvent().getIdLong());
	}
}
