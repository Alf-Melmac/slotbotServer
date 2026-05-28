package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.EventDiscordInformationBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * This requires Intents
 * <ul>
 *     <li>{@link GatewayIntent#SCHEDULED_EVENTS} to listen for scheduled events</li>
 * </ul>
 *
 * @author Alf
 * @since 23.05.26
 */
@RequiredArgsConstructor
@Slf4j
public class ScheduledEventListener extends ListenerAdapter {
	private final EventBotService eventBotService;
	private final EventDiscordInformationBotService eventDiscordInformationBotService;

	@Override
	public void onScheduledEventUserAdd(@NonNull ScheduledEventUserAddEvent event) {
		final long userId = event.getUserIdLong();
		final long scheduledEventId = event.getScheduledEvent().getIdLong();
		log.trace("User {} interested to event {}", userId, scheduledEventId);

		eventBotService.interested(scheduledEventId, userId);
	}

	@Override
	public void onScheduledEventUserRemove(@NonNull ScheduledEventUserRemoveEvent event) {
		final long userId = event.getUserIdLong();
		final long scheduledEventId = event.getScheduledEvent().getIdLong();
		log.trace("User {} no longer interested in event {}", userId, scheduledEventId);

		eventBotService.interestRemoved(scheduledEventId, userId);
	}

	@Override
	public void onScheduledEventDelete(@NonNull ScheduledEventDeleteEvent event) {
		final long scheduledEventId = event.getScheduledEvent().getIdLong();
		log.trace("Event {} deleted in guild {}", scheduledEventId, event.getGuild().getId());

		eventDiscordInformationBotService.removeScheduledEvent(scheduledEventId);
	}
}
