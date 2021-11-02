package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.InteractionUtils;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
@SlashCommand(name = "archive",
		description = "Archiviert das Event des aktuellen Kanals.",
		authorization = EVENT_MANAGE)
public class ArchiveEvent implements DiscordCommand, DiscordSlashCommand {
	private final EventBotService eventBotService;
	private final DiscordProperties discordProperties;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: archiveEvent");

		archiveEvent(message.getTextChannel(), message.getGuild().getIdLong(), message.getJDA(),
				() -> MessageUtils.replyAndDelete(message, "Der konfigurierte Archivierungskanal konnte nicht gefunden werden. Schreibe deinen Administrator des Vertrauens an. Das Event wurde trotzdem archiviert."));
	}

	private void archiveEvent(@NonNull TextChannel channel, long guildId, @NonNull JDA jda, @NonNull Runnable archiveNotFound) {
		final Event event = eventBotService.findByChannelOrThrow(channel.getIdLong());
		eventBotService.archiveEvent(event.getId(), guildId);

		final Long archiveChannelId = discordProperties.getArchive(guildId);
		if (archiveChannelId == null) {
			return;
		}

		final TextChannel archiveChannel = jda.getTextChannelById(archiveChannelId);
		if (archiveChannel == null) {
			archiveNotFound.run();
			return;
		}

		sendMessage(archiveChannel, EventUtils.buildArchiveMessage(event), ignored -> channel.delete().queue());
	}

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: unslot");

		//noinspection ConstantConditions Guild only command
		archiveEvent(event.getTextChannel(), event.getGuild().getIdLong(), event.getJDA(),
				() -> InteractionUtils.reply(event, "Der konfigurierte Archivierungskanal konnte nicht gefunden werden. Schreibe deinen Administrator des Vertrauens an. Das Event wurde trotzdem archiviert."));
	}
}
