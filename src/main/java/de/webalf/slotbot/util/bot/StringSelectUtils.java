package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.StringSelectInteraction;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.components.selections.SelectOption;
import org.atteo.classindex.ClassIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.bot.CommandClassHelper.getStringSelectInteraction;

/**
 * @author Alf
 * @since 01.08.2021
 */
@UtilityClass
public final class StringSelectUtils {
	private static final Map<String, Class<?>> idToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> annotated = ClassIndex.getAnnotated(StringSelectInteraction.class);
		StreamSupport.stream(annotated.spliterator(), false)
				.forEach(selectClass -> {
					for (String menuId : getStringSelectInteraction(selectClass).value()) {
						idToClassMap.put(menuId, selectClass);
					}
				});
	}

	/**
	 * Searches for the given selection menu id the matching class annotated with {@link StringSelectInteraction}
	 *
	 * @param stringSelectId to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String stringSelectId) {
		return idToClassMap.get(stringSelectId);
	}

	/**
	 * Builds the label string for a {@link SelectOption} respecting the maximum length
	 *
	 * @param label to use
	 * @return shortened label if needed
	 */
	public static String buildSelectLabel(@NotBlank String label) {
		return label.substring(0, Math.min(label.length(), SelectOption.LABEL_MAX_LENGTH));
	}
}
