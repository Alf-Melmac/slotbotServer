package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.SlotBotService;
import de.webalf.slotbot.service.bot.UserBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author Alf
 * @since 04.01.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommandClassHelper {
	private final EventBotService eventBotService;
	private final SlotBotService slotBotService;
	private final UserBotService userBotService;
	private final DiscordProperties discordProperties;

	/**
	 * Tries to create a new constructor instance for the given {@link DiscordCommand} or {@link DiscordSlashCommand} class
	 *
	 * @param commandClass command to get constructor for
	 * @return a new instance of the declared constructor
	 * @throws IllegalArgumentException if construct couldn't be found
	 */
	public Object getConstructor(@NonNull Class<?> commandClass) throws IllegalArgumentException {
		Object constructor = null;

		for (Constructor<?> declaredConstructor : commandClass.getDeclaredConstructors()) {
			Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();

			if (parameterTypes.length == 0) {
				//CopyEmbed, DonationEmbed, EditMessage, PostMessage, Vote
				try {
					constructor = declaredConstructor.newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance without parameters for type {}", commandClass.getName(), e);
				}
				break;
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class})) {
				//AddEventToChannel, AddSlot, BlockSlot, DelSlot, EventPing, RandomSlot, RenameSlot, Slot, Unslot
				try {
					constructor = declaredConstructor.newInstance(eventBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService parameter for type {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{DiscordProperties.class})) {
				//Help
				try {
					constructor = declaredConstructor.newInstance(discordProperties);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with DiscordProperties parameter for type {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, SlotBotService.class})) {
				//Swap
				try {
					constructor = declaredConstructor.newInstance(eventBotService, slotBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService and SlotBotService parameter for type {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, DiscordProperties.class})) {
				//ArchiveEvent
				try {
					constructor = declaredConstructor.newInstance(eventBotService, discordProperties);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService and DiscordProperties parameter for type {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{UserBotService.class})) {
				//SteamId
				try {
					constructor = declaredConstructor.newInstance(userBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with UserBotService parameter for type {}", commandClass.getName(), e);
				}
			}
		}

		if (constructor == null) {
			throw new IllegalArgumentException("Couldn't find constructor for " + commandClass.getName());
		}

		return constructor;
	}

	public static Command getCommand(@NonNull Class<?> commandClass) {
		return commandClass.getAnnotation(Command.class);
	}

	public static SlashCommand getSlashCommand(@NonNull Class<?> commandClass) {
		return commandClass.getAnnotation(SlashCommand.class);
	}
}
