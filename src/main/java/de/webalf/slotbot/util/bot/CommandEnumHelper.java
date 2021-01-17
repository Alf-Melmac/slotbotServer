package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.SlotBotService;
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
public class CommandEnumHelper {
	private final EventBotService eventBotService;
	private final SlotBotService slotBotService;
	private final DiscordProperties discordProperties;

	/**
	 * Tries to create a new constructor instance for the given {@link de.webalf.slotbot.service.bot.command.DiscordCommand}
	 *
	 * @param enumCommand command to get constructor for
	 * @return a new instance of the declared constructor
	 * @throws IllegalArgumentException if construct couldn't be found
	 */
	public Object getConstructor(Class<?> enumCommand) throws IllegalArgumentException {
		Object constructor = null;

		for (Constructor<?> declaredConstructor : enumCommand.getDeclaredConstructors()) {
			Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();

			if (parameterTypes.length == 0) {
				//Admin
				try {
					constructor = declaredConstructor.newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance without parameters for type {}", enumCommand.getName(), e);
				}
				break;
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class})) {
				//AddEventToChannel, AddSlot, BlockSlot, DelSlot, EventPrint, RenameSlot, Slot, Unslot
				try {
					constructor = declaredConstructor.newInstance(eventBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventService parameter for type {}", enumCommand.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{DiscordProperties.class})) {
				//Help
				try {
					constructor = declaredConstructor.newInstance(discordProperties);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventService parameter for type {}", enumCommand.getName(), e);
				}
			} else if (Arrays.equals(parameterTypes, new Class<?>[]{EventBotService.class, SlotBotService.class})) {
				//Swap
				try {
					constructor = declaredConstructor.newInstance(eventBotService, slotBotService);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					log.error("Failed to create new constructor instance with EventService and SlotBotService parameter for type {}", enumCommand.getName(), e);
				}
			}
		}

		if (constructor == null) {
			throw new IllegalArgumentException("Couldn't find constructor for " + enumCommand.getName());
		}

		return constructor;
	}
}
