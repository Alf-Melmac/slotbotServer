package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.enums.Commands;
import de.webalf.slotbot.model.enums.Commands.CommandEnum;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.CommandEnumHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.util.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.webalf.slotbot.model.enums.Commands.Event.EVENT_JSON;
import static de.webalf.slotbot.util.StringUtils.isNotEmpty;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
import static de.webalf.slotbot.util.bot.MessageUtils.sendDmAndDeleteMessage;

/**
 * @author Alf
 * @since 01.01.2021
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageReceivedListener extends ListenerAdapter {
	private final DiscordProperties discordProperties;
	private final CommandEnumHelper commandEnumHelper;

	@Override
	public void onMessageReceived(@NonNull MessageReceivedEvent event) {
		final Message message = event.getMessage();
		final String messageText = message.getContentRaw().trim();

		//Ignore message without prefix or from other bots
		if (!messageText.startsWith(discordProperties.getPrefix()) || event.getAuthor().isBot()) return;

		log.debug("Received command: {} from {}", messageText, event.getAuthor().getId());

		final String args = messageText.substring(discordProperties.getPrefix().length()); //Remove prefix
		List<String> argList = getArgList(args);

		final CommandEnum commandEnum = Commands.get(ListUtils.shift(argList));
		//Exit if command doesn't exist
		if (commandEnum == null) return;

		final Command command = commandEnum.getAnnotation();

		if (!validate(message, argList, commandEnum, command)) {
			return;
		}

		try { //Execute command
			final Class<?> enumCommand = commandEnum.getCommand();
			enumCommand.getMethod("execute", Message.class, List.class).invoke(commandEnumHelper.getConstructor(enumCommand), message, argList);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BusinessRuntimeException || cause instanceof ForbiddenException || cause instanceof ResourceNotFoundException) {
				if (StringUtils.isNotEmpty(cause.getMessage())) {
					replyAndDelete(message, cause.getMessage());
				} else {
					replyAndDelete(message, "Das gesuchte Element kann nicht erreicht werden.");
				}
			} else {
				unknownException(message, argList, commandEnum, e);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			unknownException(message, argList, commandEnum, e);
		}
	}

	/**
	 * Command usage validation
	 *
	 * @return true if validation succeeded
	 */
	private boolean validate(Message message, List<String> argList, CommandEnum commandEnum, Command command) {
		if (!ArrayUtils.contains(ListUtils.convertToBoxedArray(command.argCount()), argList.size())) {
			wrongArgumentCount(message, argList, command);
			return false;
		} else if (!commandEnum.isAllowed(message)) {
			replyAndDelete(message, "Das darfst du hier nicht.");
			return false;
		}
		return true;
	}

	/**
	 * Returns the arguments as a list
	 *
	 * @param args all arguments
	 * @return arguments {@link StringUtils#splitOnSpacesExceptQuotes(String)} or only command and arguments as second string for special commands
	 */
	private List<String> getArgList(@NonNull String args) {
		List<String> argList;
		if (Arrays.stream(EVENT_JSON.getAnnotation().names()).noneMatch(args.toLowerCase()::matches)) {
			argList = StringUtils.splitOnSpacesExceptQuotes(args);
		} else {
			//For these special commands everything after the command is handled as one argument. Validation and potential splitting must be done by the position called up
			argList = new ArrayList<>(Arrays.asList(args.split(" ", 2)));
		}
		return argList;
	}

	/**
	 * Error handling for wrong argument count received
	 *
	 * @param message that triggered the command
	 * @param argList given arguments
	 * @param command desired command
	 */
	private void wrongArgumentCount(Message message, List<String> argList, Command command) {
		log.debug("{} arguments received didn't meet the expectation: {}", argList.size(), command.argCount());
		String reply = "Das ist die falsche Zahl Argumente!";
		if (isNotEmpty(command.usage())) {
			reply += "\nSo funktioniert der Befehl: `" + discordProperties.getPrefix() + command.names()[0] + " " + command.usage() + "`";
		}
		sendDmAndDeleteMessage(message, reply);
	}

	private void unknownException(Message message, List<String> argList, @NonNull CommandEnum commandEnum, ReflectiveOperationException e) {
		log.error("Failed to execute command {} with arguments {}", commandEnum.toString(), argList, e);
		replyAndDelete(message, "Tja, da ist wohl was schief gelaufen.");
	}
}