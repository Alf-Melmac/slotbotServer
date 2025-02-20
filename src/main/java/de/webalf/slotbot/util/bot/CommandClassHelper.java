package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.*;
import de.webalf.slotbot.service.bot.*;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.service.bot.command.DiscordStringSelect;
import de.webalf.slotbot.service.bot.command.DiscordUserContext;
import de.webalf.slotbot.util.EventHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class CommandClassHelper {
	private final EventBotService eventBotService;
	private final EventHelper eventHelper;
	private final GuildBotService guildBotService;
	private final RequirementBotService requirementBotService;
	private final SwapRequestBotService swapRequestBotService;
	private final GuildUsersBotService guildUsersBotService;

	/**
	 * Tries to create a new constructor instance for the given {@link DiscordSlashCommand}, {@link DiscordStringSelect} or {@link DiscordUserContext} class
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
				//CopyEmbed, PostMessage, Vote
				try {
					constructor = declaredConstructor.newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance without parameters for class {}", commandClass.getName(), e);
				}
				break;
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class})) {
				//AddSlot, ArchiveEvent, BlockSlot, DelSlot, EventPing, RandomSlot, RenameSlot, Unslot
				try {
					constructor = declaredConstructor.newInstance(eventBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService parameter for class {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, EventHelper.class, GuildBotService.class})) {
				//AddEventToChannel
				try {
					constructor = declaredConstructor.newInstance(eventBotService, eventHelper, guildBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService, EventHelper and GuildBotService parameter for class {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, GuildBotService.class})) {
				//RebuildArchive
				try {
					constructor = declaredConstructor.newInstance(eventBotService, guildBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService and GuildBotService parameter for class {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, RequirementBotService.class})) {
				//Slot
				try {
					constructor = declaredConstructor.newInstance(eventBotService, requirementBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService and RequirementBotService parameter for class {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{SwapRequestBotService.class})) {
				//Swap
				try {
					constructor = declaredConstructor.newInstance(swapRequestBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventBotService and SlotBotService parameter for class {}", commandClass.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{GuildUsersBotService.class})) {
				//AddUserToGuild
				try {
					constructor = declaredConstructor.newInstance(guildUsersBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with GuildUsersBotService parameter for class {}", commandClass.getName(), e);
				}
			}
		}

		if (constructor == null) {
			throw new IllegalArgumentException("Couldn't find constructor for " + commandClass.getName());
		}

		return constructor;
	}

	/**
	 * Get all {@link SlashCommand} annotations on the given class
	 *
	 * @param commandClass command class
	 * @return all slash commands
	 */
	public static SlashCommand[] getSlashCommand(@NonNull Class<?> commandClass) {
		final SlashCommand slashCommand = commandClass.getAnnotation(SlashCommand.class);
		if (slashCommand == null) {
			return commandClass.getAnnotation(SlashCommands.class).value();
		}
		return new SlashCommand[]{slashCommand};
	}

	public static ContextMenu getContextMenu(@NonNull Class<?> menuClass) {
		return menuClass.getAnnotation(ContextMenu.class);
	}

	public static StringSelectInteraction getStringSelectInteraction(@NonNull Class<?> selectClass) {
		return selectClass.getAnnotation(StringSelectInteraction.class);
	}

	/**
	 * Get all {@link ButtonInteraction} annotations on the given class
	 *
	 * @param buttonClass button class
	 * @return all button interactions
	 */
	public static ButtonInteraction[] getButtonInteraction(@NonNull Class<?> buttonClass) {
		final ButtonInteraction buttonInteraction = buttonClass.getAnnotation(ButtonInteraction.class);
		if (buttonInteraction == null) {
			return buttonClass.getAnnotation(ButtonInteractions.class).value();
		}
		return new ButtonInteraction[]{buttonInteraction};
	}
}
