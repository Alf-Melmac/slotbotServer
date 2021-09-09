package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getStringOption;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 21.02.2021
 */
@Slf4j
@Command(names = {"postMessage", "post", "messagePost"},
		description = "Sendet die übergebene Nachricht in den gleichen Kanal.",
		usage = "\"<Nachricht>\"",
		argCount = {1},
		authorization = EVENT_MANAGE)
@SlashCommand(name = "postMessage",
		description = "Lässt den Bot die übergebene Nachricht in den gleichen Kanal versenden.",
		authorization = EVENT_MANAGE,
		optionPosition = 0)
public class PostMessage implements DiscordCommand, DiscordSlashCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: postMessage");

		sendMessage(message, args.get(0));

		deleteMessagesInstant(message);
	}

	private static final String OPTION_MESSAGE = "nachricht";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.STRING, OPTION_MESSAGE, "Zu versendender Text.", true))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: postMessage");

		@SuppressWarnings("ConstantConditions") //Required option
		final String message = getStringOption(event.getOption(OPTION_MESSAGE));

		sendMessage(event, message);

		finishedSlashCommandAction(event);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
