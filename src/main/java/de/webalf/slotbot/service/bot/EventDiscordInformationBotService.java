package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.service.EventDiscordInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Wrapper for {@link EventDiscordInformationService} to be used by discord bot
 *
 * @author Alf
 * @since 23.05.26
 */
@Service
@RequiredArgsConstructor
public class EventDiscordInformationBotService {
	private final EventDiscordInformationService eventDiscordInformationService;

	public void removeByChannel(long guildId, long channelId) {
		eventDiscordInformationService.removeByChannel(guildId, channelId);
	}

	public void removeByMessage(long channelId, long messageId, Consumer<EventDiscordInformation> onRemoval) {
		eventDiscordInformationService.removeByMessage(channelId, messageId, onRemoval);
	}

	public void removeByGuild(long guildId) {
		eventDiscordInformationService.removeByGuild(guildId);
	}

	public void removeScheduledEvent(long scheduledEventId) {
		eventDiscordInformationService.removeScheduledEvent(scheduledEventId);
	}
}
