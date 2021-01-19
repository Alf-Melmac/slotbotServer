package de.webalf.slotbot.model.enums;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.event.*;
import de.webalf.slotbot.service.bot.command.util.Admin;
import de.webalf.slotbot.service.bot.command.util.Help;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static de.webalf.slotbot.util.PermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.PermissionHelper.isAuthorized;

/**
 * @author Alf
 * @since 01.01.2021
 */
public class Commands {
	public interface CommandEnum {
		Class<?> getCommand();

		default String getDefaultName() {
			return getAnnotation().names()[0];
		}

		/**
		 * Checks if the author if the given message is allowed to execute this command
		 *
		 * @param message that triggers the command
		 * @return true if the message author is allowed
		 */
		default boolean isAllowed(@NonNull Message message) {
			final Command command = getAnnotation();
			final boolean dm = MessageUtils.isDm(message);
			return dm && command.dmAllowed() || //DM and allowed in DM
					!dm && command.authorization() == NONE ||
					!dm && isAuthorized(command.authorization(), message); //Not in dm and has any of the authorized roles
		}

		default Command getAnnotation() {
			return getCommand().getAnnotation(Command.class);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum Util implements CommandEnum {
		ADMIN(Admin.class),
		HELP(Help.class);

		private final Class<?> command;
	}

	@Getter
	@AllArgsConstructor
	public enum Event implements CommandEnum {
		ADD_EVENT_TO_CHANNEL(AddEventToChannel.class),
		ADD_SLOT(AddSlot.class),
		BLOCK_SLOT(BlockSlot.class),
		DEL_SLOT(DelSlot.class),
		EVENT_JSON(Admin.class),
		EVENT_PRINT(EventPrint.class),
		RENAME_SLOT(RenameSlot.class),
		RENAME_SQUAD(RenameSquad.class),
		SLOT(Slot.class),
		SWAP(Swap.class),
		UNSLOT(Unslot.class);

		private final Class<?> command;
	}

	private static final Map<String, CommandEnum> commandToEnumMap = new HashMap<>();

	static {
		for (Util commandEnum : EnumSet.allOf(Util.class)) {
			for (String command : getCommandNames(commandEnum)) {
				commandToEnumMap.put(command.toLowerCase(), commandEnum);
			}
		}
		for (Event commandEnum : EnumSet.allOf(Event.class)) {
			for (String command : getCommandNames(commandEnum)) {
				commandToEnumMap.put(command.toLowerCase(), commandEnum);
			}
		}
	}

	/**
	 * Searches for the matching {@link CommandEnum} for the given command
	 *
	 * @param command to search
	 * @return matching commandEnum or null if not found
	 */
	public static CommandEnum get(@NonNull String command) {
		return commandToEnumMap.get(command.toLowerCase());
	}

	private static String[] getCommandNames(@NonNull CommandEnum commandEnum) {
		return commandEnum.getCommand().getAnnotation(Command.class).names();
	}
}
