package de.webalf.slotbot.model.enums;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.command.*;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.webalf.slotbot.util.PermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.PermissionHelper.isAuthorized;

/**
 * @author Alf
 * @since 01.01.2021
 */
public class Commands {
	public interface CommandEnum {
		Set<String> getCommands();

		Class<?> getCommand();

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
		ADMIN(Set.of("admin"), Admin.class),
		HELP(Set.of("help", "commands"), Help.class);

		private final Set<String> commands;
		private final Class<?> command;
	}

	@Getter
	@AllArgsConstructor
	public enum Event implements CommandEnum {
		ADD_EVENT_TO_CHANNEL(Set.of("addeventtochannel", "addchannel", "addevent"), AddEventToChannel.class),
		ADD_SLOT(Set.of("addslot", "eventaddslot", "slotadd"), AddSlot.class),
		BLOCK_SLOT(Set.of("blockslot", "slotblock"), BlockSlot.class),
//		DEL_EVENT(Set.of("delevent", "eventdel", "deleteevent", "removeevent"), Admin.class),
		DEL_SLOT(Set.of("delslot", "eventdelslot", "deleteslot", "removeslot", "slotdel", "slotremove"), DelSlot.class),
		EVENT_JSON(Set.of("eventjson", "event", "newevent", "createevent"), Admin.class),
		EVENT_PRINT(Set.of("eventprint", "showevent", "printevent"), EventPrint.class),
//		EVENT_UPDATE(Set.of("eventupdate", "eventrefresh", "updateevent"), Admin.class),
//		RENAME_SLOT(Set.of("renameslot", "editslot", "eventrenameslot"), Admin.class),
		SLOT(Set.of("slot", "forceslot"), Slot.class),
//		SWAP(Set.of("swap"), Admin.class),
		UNSLOT(Set.of("unslot", "forceunslot"), Unslot.class);

		private final Set<String> commands;
		private final Class<?> command;
	}

	private static final Map<String, CommandEnum> commandToEnumMap = new HashMap<>();

	static {
		for (Util commandEnum : EnumSet.allOf(Util.class)) {
			for (String command : commandEnum.getCommands()) {
				commandToEnumMap.put(command, commandEnum);
			}
		}
		for (Event commandEnum : EnumSet.allOf(Event.class)) {
			for (String command : commandEnum.getCommands()) {
				commandToEnumMap.put(command, commandEnum);
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
}
