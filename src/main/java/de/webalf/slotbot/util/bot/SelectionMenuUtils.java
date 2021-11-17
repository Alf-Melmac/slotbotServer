package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.SelectionMenuListener;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.atteo.classindex.ClassIndex;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 01.08.2021
 */
@UtilityClass
public final class SelectionMenuUtils {
	private static final Map<String, Class<?>> idToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> classIterable = ClassIndex.getAnnotated(SelectionMenuListener.class);
		StreamSupport.stream(classIterable.spliterator(), false)
				.forEach(command -> {
					for (String menuId : command.getAnnotation(SelectionMenuListener.class).value()) {
						idToClassMap.put(menuId, command);
					}
				});
	}

	/**
	 * Searches for the given selection menu id the matching class annotated with {@link SelectionMenuListener}
	 *
	 * @param selectionMenuId to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String selectionMenuId) {
		return idToClassMap.get(selectionMenuId);
	}

	/**
	 * Builds the label string for a {@link SelectOption} respecting the maximum length
	 *
	 * @param label to use
	 * @return shortened label if needed
	 */
	public static String buildSelectionLabel(@NotBlank String label) {
		return label.substring(0, Math.min(label.length(), SelectOption.LABEL_MAX_LENGTH));
	}
}
