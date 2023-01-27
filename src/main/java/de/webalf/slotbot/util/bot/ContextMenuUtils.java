package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.ContextMenu;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.atteo.classindex.ClassIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.bot.DiscordLocaleHelper.DEFAULT_LOCALE;

/**
 * @author Alf
 * @since 11.12.2022
 */
@UtilityClass
public final class ContextMenuUtils {
	public static final Map<String, Class<?>> commandToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(ContextMenu.class);
		StreamSupport.stream(commandList.spliterator(), false)
				.forEach(command -> commandToClassMap.put(DEFAULT_LOCALE.t(CommandClassHelper.getContextMenu(command).name()).toLowerCase(), command));
	}

	/**
	 * Searches for the given context menu the matching class annotated with {@link ContextMenu}
	 *
	 * @param command to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String command) {
		return commandToClassMap.get(command.toLowerCase());
	}
}
