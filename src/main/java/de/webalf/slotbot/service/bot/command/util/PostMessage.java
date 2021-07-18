package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 21.02.2021
 */
@Slf4j
@Command(names = {"postMessage", "post", "messagePost"},
		description = "Sendet die übergebene Nachricht in den gleichen Kanal.",
		usage = "<Nachricht>",
		argCount = {1},
		authorization = EVENT_MANAGE)
public class PostMessage implements DiscordCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: postMessage");

		message.getChannel().sendMessage(args.get(0)).queue();

		deleteMessagesInstant(message);
	}
}
