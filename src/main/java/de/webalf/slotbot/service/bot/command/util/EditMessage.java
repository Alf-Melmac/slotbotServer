package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyErrorMessage;

/**
 * @author Alf
 * @since 21.02.2021
 */
@Slf4j
@Command(names = {"editMessage", "edit", "messageEdit"},
		description = "Bearbeitet die übergebene Bot-Nachricht. Wurde z.B. vorher mit postMessage verschickt.",
		usage = "<MessageId> <NeuerText>",
		argCount = {2})
public class EditMessage implements DiscordCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: editMessage");

		message.getChannel().editMessageById(args.get(0), args.get(1)).queue(unused -> {}, replyErrorMessage(message));

		deleteMessagesInstant(message);
	}
}
