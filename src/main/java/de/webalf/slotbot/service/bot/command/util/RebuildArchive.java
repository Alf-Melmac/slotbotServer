package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.SYS_ADMINISTRATION;

/**
 * @author Alf
 * @since 07.07.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"rebuildArchive"},
		description = "Sendet alle Archivnachrichten erneut in den Archiv-Kanal.",
		authorization = SYS_ADMINISTRATION)
public class RebuildArchive implements DiscordCommand {
	private final EventBotService eventBotService;
	private final DiscordProperties discordProperties;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: rebuildArchive");

		final Long archiveChannelId = discordProperties.getArchive();
		if (archiveChannelId == null) {
			return;
		}

		final TextChannel archiveChannel = message.getJDA().getTextChannelById(archiveChannelId);
		if (archiveChannel == null) {
			MessageUtils.replyAndDelete(message, "Der konfigurierte Archivierungskanal konnte nicht gefunden werden. Schreibe deinen Administrator des Vertrauens an.");
			return;
		}

		eventBotService.findAllInPast().forEach(event -> archiveChannel.sendMessage(EventUtils.buildArchiveMessage(event)).queue());
	}
}
