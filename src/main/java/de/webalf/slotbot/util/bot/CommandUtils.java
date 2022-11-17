package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.Command;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import org.atteo.classindex.ClassIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.isAuthorized;

/**
 * @author Alf
 * @since 01.01.2021
 */
@UtilityClass
public final class CommandUtils {
	private static final Map<String, Class<?>> commandToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(Command.class);
		StreamSupport.stream(commandList.spliterator(), false)
				.forEach(command -> {
					for (String commandName : CommandClassHelper.getCommand(command).names()) {
						commandToClassMap.put(commandName.toLowerCase(), command);
					}
				});
	}

	/**
	 * Searches for the given command the matching class annotated with {@link Command}
	 *
	 * @param command to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String command) {
		return commandToClassMap.get(command.toLowerCase());
	}

	/**
	 * Checks if the author of the given message is allowed to execute this command
	 *
	 * @param command to check permission for
	 * @param message that triggers the command
	 * @return true if the message author is allowed
	 */
	public static boolean isAllowed(@NonNull Command command, Message message) {
		final boolean dm = MessageUtils.isDm(message);
		return dm && command.dmAllowed() || //DM and allowed in DM
				!dm && command.authorization() == NONE ||
				!dm && isAuthorized(command.authorization(), message); //Not in dm and has any of the authorized roles
	}
}
