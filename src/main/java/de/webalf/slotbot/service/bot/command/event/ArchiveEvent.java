package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 07.07.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"archiveEvent", "archive", "eventArchive"},
		description = "Archiviert das Event des aktuellen Kanals.",
		authorization = EVENT_MANAGE)
public class ArchiveEvent implements DiscordCommand {
	private final EventBotService eventBotService;
	private final DiscordProperties discordProperties;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: archiveEvent");

		final MessageChannel channel = message.getChannel();
		eventBotService.findByChannel(message, channel.getIdLong())
				.ifPresent(archiveEventConsumer(message));
	}

	private Consumer<Event> archiveEventConsumer(Message message) {
		return event -> {
			eventBotService.archiveEvent(event.getId(), message.getGuild().getIdLong());
			discordArchive(message, event);
		};
	}

	private void discordArchive(@NonNull Message message, Event archivedEvent) {
		final Long archiveChannelId = discordProperties.getArchive(message.getGuild().getIdLong());
		if (archiveChannelId == null) {
			return;
		}

		final TextChannel archiveChannel = message.getJDA().getTextChannelById(archiveChannelId);
		if (archiveChannel == null) {
			MessageUtils.replyAndDelete(message, "Der konfigurierte Archivierungskanal konnte nicht gefunden werden. Schreibe deinen Administrator des Vertrauens an.");
			return;
		}

		sendMessage(archiveChannel, EventUtils.buildArchiveMessage(archivedEvent), ignored -> message.getTextChannel().delete().queue());
	}
}
