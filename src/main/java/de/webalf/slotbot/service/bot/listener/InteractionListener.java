package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static de.webalf.slotbot.util.bot.InteractionUtils.*;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InteractionListener extends ListenerAdapter {
	private final CommandClassHelper commandClassHelper;
	private final MessageSource messageSource;

	@Override
	public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
		final String commandName = event.getName();
		log.debug("Received slash command: '{}' from {}", event.getCommandString(), event.getUser().getId());

		final DiscordLocaleHelper locale = new DiscordLocaleHelper(event.getUserLocale(), messageSource);
		final Class<?> commandClass = SlashCommandUtils.get(commandName);
		if (commandClass == null) {
			log.error("Received not known slash command: {}", commandName);
			reply(event, locale.t("bot.interaction.response.unknown", commandName));
			return;
		}

		ephemeralDeferReply(event);

		try {
			commandClass.getMethod("execute", SlashCommandInteractionEvent.class, DiscordLocaleHelper.class)
					.invoke(commandClassHelper.getConstructor(commandClass), event, locale);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof BusinessRuntimeException || cause instanceof ForbiddenException || cause instanceof ResourceNotFoundException) {
				if (StringUtils.isNotEmpty(cause.getMessage())) {
					reply(event, cause.getMessage());
				} else {
					reply(event, locale.t("bot.slash.response.notFound"));
				}
			} else {
				unknownException(event, commandClass, e);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			unknownException(event, commandClass, e);
		}
	}

	private void unknownException(@NonNull GenericCommandInteractionEvent event, @NonNull Class<?> commandClass, ReflectiveOperationException e) {
		final String errorCode = getErrorCode(e);
		log.error("Failed to execute command interaction {} with options {} - {}", commandClass.getName(), event.getOptions(), errorCode, e);
		failedInteraction(event, "Sorry. Error Code: `" + errorCode + "`");
	}

	@Override
	public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
		final String componentId = event.getComponentId();
		log.debug("Received selection menu event: {} from {}", componentId, event.getUser().getId());

		final DiscordLocaleHelper locale = new DiscordLocaleHelper(event.getUserLocale(), messageSource);
		final Class<?> aClass = StringSelectUtils.get(componentId);
		if (aClass == null) {
			log.error("Received not known selection menu: {}", componentId);
			reply(event, locale.t("bot.interaction.response.unknown", componentId));
			return;
		}

		deferEdit(event);

		try {
			aClass.getMethod("process", StringSelectInteractionEvent.class, DiscordLocaleHelper.class)
					.invoke(commandClassHelper.getConstructor(aClass), event, locale);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BusinessRuntimeException || cause instanceof ForbiddenException || cause instanceof ResourceNotFoundException) {
				if (StringUtils.isNotEmpty(cause.getMessage())) {
					replyAndRemoveComponents(event, cause.getMessage());
				} else {
					replyAndRemoveComponents(event, "Das gesuchte Element kann nicht erreicht werden.");
				}
			} else {
				unknownException(event, aClass, e);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			unknownException(event, aClass, e);
		}
	}

	private void unknownException(@NonNull StringSelectInteractionEvent event, @NonNull Class<?> commandClass, ReflectiveOperationException e) {
		final String errorCode = getErrorCode(e);
		log.error("Failed to process string selection menu selection {} with id {} - {}", commandClass.getName(), event.getComponentId(), errorCode, e);
		replyAndRemoveComponents(event, "Sorry. Error Code: `" + errorCode + "`");
	}

	@Override
	public void onUserContextInteraction(@NonNull UserContextInteractionEvent event) {
		final String commandName = event.getName();
		log.debug("Received user context interaction event: {} from {}", commandName, event.getUser().getId());

		final Class<?> commandClass = ContextMenuUtils.get(commandName);
		if (commandClass == null) {
			log.error("Received not known context menu: {}", commandName);
			return;
		}

		ephemeralDeferReply(event);

		final DiscordLocaleHelper locale = new DiscordLocaleHelper(event.getUserLocale(), messageSource);
		try {
			commandClass.getMethod("perform", UserContextInteractionEvent.class, DiscordLocaleHelper.class)
					.invoke(commandClassHelper.getConstructor(commandClass), event, locale);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			unknownException(event, commandClass, e);
		}
	}

	private String getErrorCode(ReflectiveOperationException e) {
		final String message = e.getMessage();
		return message != null ? UUID.nameUUIDFromBytes(message.getBytes()).toString() : UUID.randomUUID().toString();
	}
}
