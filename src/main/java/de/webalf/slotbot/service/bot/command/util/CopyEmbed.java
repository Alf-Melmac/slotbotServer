package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

import static de.webalf.slotbot.util.bot.MentionUtils.getId;
import static de.webalf.slotbot.util.bot.MentionUtils.isChannelMention;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.ADMINISTRATIVE;

/**
 * @author Alf
 * @since 21.02.2021
 */
@Slf4j
@Command(names = {"copyEmbed", "embedCopy"},
		description = "Kopiert ein Embed einer Nachricht im aktuellen Kanal in einen anderen Kanal.",
		usage = "<MessageId des zu kopierenden Embeds> <NeuerKanal>",
		argCount = {2},
		authorization = ADMINISTRATIVE)
public class CopyEmbed implements DiscordCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: copyEmbed");

		String channelMention = args.get(1);
		if (!isChannelMention(channelMention)) {
			replyAndDeleteOnlySend(message, "Du musst an zweiter Stelle einen Kanal angeben.");
		} else {
			message.getTextChannel()
					.retrieveMessageById(args.get(0))
					.queue(messageWithEmbedToCopy -> {
						final MessageEmbed embed = messageWithEmbedToCopy.getEmbeds()
								.stream().findAny().orElseThrow(ResourceNotFoundException::new);
						final TextChannel channel = message.getJDA().getTextChannelById(getId(channelMention));
						if (channel == null) {
							replyAndDelete(message, "Dieser Textkanal konnte nicht gefunden werden: " + getId(channelMention));
							return;
						}
						channel.sendMessageEmbeds(embed).queue();
					}, replyErrorMessage(message));
		}

		deleteMessagesInstant(message);
	}
}
