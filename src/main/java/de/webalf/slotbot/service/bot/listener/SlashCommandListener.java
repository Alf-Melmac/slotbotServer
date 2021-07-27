package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import de.webalf.slotbot.util.bot.SlashCommandUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

import static de.webalf.slotbot.util.bot.InteractionUtils.ephemeralDeferReply;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlashCommandListener extends ListenerAdapter {
	private final CommandClassHelper commandClassHelper;

	@Override
	public void onSlashCommand(@NonNull SlashCommandEvent event) {
		final String commandName = event.getName();
		log.debug("Received slash command: {} from {}", commandName, event.getUser().getId());

		final Class<?> commandClass = SlashCommandUtils.get(commandName);
		if (commandClass == null) {
			log.error("Received not known slash command: {}", commandName);
			return;
		}

		ephemeralDeferReply(event);

		try {
			commandClass.getMethod("execute", SlashCommandEvent.class).invoke(commandClassHelper.getConstructor(commandClass), event);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof BusinessRuntimeException || cause instanceof ForbiddenException || cause instanceof ResourceNotFoundException) {
				if (StringUtils.isNotEmpty(cause.getMessage())) {
					reply(event, cause.getMessage());
				} else {
					reply(event, "Das gesuchte Element kann nicht erreicht werden.");
				}
			} else {
				unknownException(event, commandClass, e);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			unknownException(event, commandClass, e);
		}
	}

	private void unknownException(SlashCommandEvent event, @NonNull Class<?> commandClass, ReflectiveOperationException e) {
		log.error("Failed to execute slash command {} with options {}", commandClass.getName(), event.getOptions(), e);
		reply(event, "Tja, da ist wohl was schief gelaufen.");
	}
}
