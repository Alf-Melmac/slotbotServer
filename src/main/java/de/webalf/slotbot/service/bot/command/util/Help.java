package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.CommandUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.atteo.classindex.ClassIndex;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.ListUtils.zeroArguments;
import static de.webalf.slotbot.util.bot.CommandClassHelper.getCommand;
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

	private static final Command THIS_COMMAND = getCommand(Help.class);

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

		final Class<?> commandClass = CommandUtils.get(args.get(0));

		if (commandClass == null) {
			replyAndDelete(message, "Diesen Befehl kenne ich nicht.");
			return;
		}

		final Command command = getCommand(commandClass);
		if (!CommandUtils.isAllowed(command, message) && !isDm(message)) {
			replyAndDelete(message, "Den Befehl " + command.names()[0] + " darfst du hier nicht ausführen.");
			return;
		}

		final List<String> names = Stream.of(command.names()).collect(Collectors.toList());
		final String mainName = ListUtils.shift(names);
		StringBuilder help = new StringBuilder("**Name:** " + mainName);

		if (StringUtils.isNotEmpty(command.description())) {
			help.append("\n**Beschreibung:** ").append(command.description());
		}

		if (!names.isEmpty()) {
			help.append("\n**Alternative Benennungen:** ").append(ListUtils.shift(names));
			for (String name : names) {
				help.append(", ").append(name);
			}
		}

		help.append("\n**Benutzung:** `").append(discordProperties.getPrefix()).append(mainName);
		if (Arrays.stream(command.argCount()).anyMatch(number -> number != 0)) {
			help.append(" ").append(command.usage());
		}
		help.append("`");

		sendDmAndDeleteMessage(message, help.toString());
	}

	private String getAllAllowedCommands(Message message) {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(Command.class);
		return StreamSupport.stream(commandList.spliterator(), true)
				.filter(command -> CommandUtils.isAllowed(getCommand(command), message))
				.map(command -> getCommand(command).names()[0])
				.collect(Collectors.joining(", "));
	}
}
