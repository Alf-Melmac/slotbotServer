package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.annotations.bot.Command;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import de.webalf.slotbot.util.bot.CommandUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.IntStream;

import static de.webalf.slotbot.util.StringUtils.isNotEmpty;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
import static de.webalf.slotbot.util.bot.MessageUtils.sendDmAndDeleteMessage;

/**
 * @author Alf
 * @since 01.01.2021
 */
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class MessageReceivedListener extends ListenerAdapter {
	private final DiscordProperties discordProperties;
	private final CommandClassHelper commandClassHelper;

	@Override
	public void onMessageReceived(@NonNull MessageReceivedEvent event) {
		final Message message = event.getMessage();
		final String messageText = message.getContentRaw().trim();

		//Ignore message without prefix or from other bots and remove prefix from args
		String args;
		if (messageText.startsWith(discordProperties.getPrefix())) {
			args = messageText.substring(discordProperties.getPrefix().length());
		} else if (messageText.startsWith("/")) {
			args = messageText.substring(1);
		} else {
			return;
		}

		log.debug("Received command: '{}' from {}", messageText, event.getAuthor().getId());

		List<String> argList = getArgList(args);

		if (argList.isEmpty()) return;
		@SuppressWarnings("ConstantConditions") //Checked with if above
		final Class<?> commandClass = CommandUtils.get(ListUtils.shift(argList));
		//Exit if command doesn't exist
		if (commandClass == null) return;

		final Command command = CommandClassHelper.getCommand(commandClass);
		if (!validate(message, argList, command)) {
			return;
		}

		try { //Execute command
			commandClass.getMethod("execute", Message.class, List.class).invoke(commandClassHelper.getConstructor(commandClass), message, argList);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BusinessRuntimeException || cause instanceof ForbiddenException || cause instanceof ResourceNotFoundException) {
				if (StringUtils.isNotEmpty(cause.getMessage())) {
					replyAndDelete(message, cause.getMessage());
				} else {
					replyAndDelete(message, "Das gesuchte Element kann nicht erreicht werden.");
				}
			} else {
				unknownException(message, argList, commandClass, e);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			unknownException(message, argList, commandClass, e);
		}
	}

	/**
	 * Command usage validation
	 *
	 * @return true if validation succeeded
	 */
	private boolean validate(Message message, @NonNull List<String> argList, @NonNull Command command) {
		if (IntStream.of(command.argCount()).noneMatch(x -> x == argList.size())) {
			wrongArgumentCount(message, argList, command);
			return false;
		} else if (!CommandUtils.isAllowed(command, message)) {
			replyAndDelete(message, "Das darfst du hier nicht.");
			return false;
		}
		return true;
	}

	/**
	 * Returns the arguments as a list
	 *
	 * @param args all arguments
	 * @return arguments {@link StringUtils#splitOnSpacesExceptQuotes(String)}
	 */
	private List<String> getArgList(@NonNull String args) {
		return StringUtils.splitOnSpacesExceptQuotes(args);
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

	private void unknownException(Message message, List<String> argList, @NonNull Class<?> commandClass, ReflectiveOperationException e) {
		log.error("Failed to execute command {} with arguments {}", commandClass.getName(), argList, e);
		replyAndDelete(message, "Tja, da ist wohl was schief gelaufen.");
	}
}