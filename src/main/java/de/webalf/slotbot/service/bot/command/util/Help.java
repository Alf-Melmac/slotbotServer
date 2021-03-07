package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.bot.Commands;
import de.webalf.slotbot.model.bot.Commands.CommandEnum;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.atteo.classindex.ClassIndex;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.ListUtils.zeroArguments;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

/**
 * @author Alf
 * @since 02.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"help", "commands"},
		description = "Gibt alle Befehle oder Informationen über einen Befehl aus.",
		usage = "(<Befehlsname>)",
		argCount = {0, 1},
		authorization = NONE,
		dmAllowed = true)
public class Help implements DiscordCommand {
	private final DiscordProperties discordProperties;

	private static final Command THIS_COMMAND = Help.class.getAnnotation(Command.class);

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: help");

		if (zeroArguments(args)) {
			if (isDm(message)) {
				sendDmAndDeleteMessage(message, "Um die richtigen Befehle anzeigen zu können, muss dieser Befehl auf dem Server ausgeführt werden.");
			} else {
				sendDmAndDeleteMessage(message, "Liste aller verfügbaren Befehle:\n" + getAllAllowedCommands(message) + "\nSchicke einfach `" + discordProperties.getPrefix() + THIS_COMMAND.names()[0] + " " + THIS_COMMAND.usage().replaceAll("[()]", "") + "`, um Infos über einen bestimmten Befehl zu bekommen.");
			}
			return;
		}

		final CommandEnum commandEnum = Commands.get(args.get(0));

		if (commandEnum == null) {
			replyAndDelete(message, "Diesen Befehl kenne ich nicht.");
			return;
		} else if (!commandEnum.isAllowed(message) && !isDm(message)) {
			replyAndDelete(message, "Den Befehl " + commandEnum.getDefaultName() +  " darfst du hier nicht ausführen.");
			return;
		}

		final Command command = commandEnum.getAnnotation();
		String help = "**Name:** " + command.names()[0];

		if (StringUtils.isNotEmpty(command.description())) {
			help += "\n**Beschreibung:** " + command.description();
		}

		help += "\n**Benutzung:** " + discordProperties.getPrefix() + command.names()[0];
		if (Arrays.stream(command.argCount()).anyMatch(number -> number != 0)) {
			help += " " + command.usage();
		}

		sendDmAndDeleteMessage(message, help);
	}

	private String getAllAllowedCommands(Message message) {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(Command.class);
		return StreamSupport.stream(commandList.spliterator(), true)
				.filter(command -> Commands.get(command.getAnnotation(Command.class).names()[0]).isAllowed(message))
				.map(command -> command.getAnnotation(Command.class).names()[0])
				.collect(Collectors.joining(", "));
	}
}
