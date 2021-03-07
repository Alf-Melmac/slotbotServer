package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyErrorMessage;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

/**
 * @author Alf
 * @since 21.02.2021
 */
@Slf4j
@Command(names = {"vote"},
		description = "Reagiert auf die übergebene Nachricht mit Daumen hoch und runter. Die Nachricht muss im gleichen Kanal sein.",
		usage = "<MessageId>",
		argCount = {1},
		authorization = NONE)
public class Vote implements DiscordCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: vote");

		final String messageId = args.get(0);
		final MessageChannel channel = message.getChannel();
		channel.addReactionById(messageId, THUMBS_UP).queue(unused ->
						channel.addReactionById(messageId, THUMBS_DOWN).queue(),
				replyErrorMessage(message));

		deleteMessagesInstant(message);
	}
}
