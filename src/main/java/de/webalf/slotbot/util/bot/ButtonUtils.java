package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.ButtonInteraction;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.atteo.classindex.ClassIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.bot.CommandClassHelper.getButtonInteraction;

/**
 * @author Alf
 * @since 22.08.2023
 */
@UtilityClass
public final class ButtonUtils {
	private static final Map<String, Class<?>> idPrefixToClassMap = new HashMap<>();
	private static final String BUTTON_ID_SEPARATOR = "-";

	static {
		final Iterable<Class<?>> annotated = ClassIndex.getAnnotated(ButtonInteraction.class);
		StreamSupport.stream(annotated.spliterator(), false)
				.forEach(buttonClass -> {
					for (ButtonInteraction buttonInteraction : getButtonInteraction(buttonClass)) {
						idPrefixToClassMap.put(buttonInteraction.value(), buttonClass);
					}
				});
	}

	/**
	 * Searches for the given context menu the matching class annotated with {@link ButtonInteraction}
	 *
	 * @param buttonId to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String buttonId) {
		return idPrefixToClassMap.get(buttonId.substring(0, buttonId.indexOf(BUTTON_ID_SEPARATOR)).toLowerCase());
	}

	/**
	 * Builds a button id from the given prefix and interaction id.
	 * The prefix is used to identify the class annotated with {@link ButtonInteraction}.
	 * The interaction id is used to identify different interactions of the same class.
	 *
	 * @see #splitButtonId(ButtonInteractionEvent)
	 */
	public static String buildButtonId(@NonNull String buttonIdPrefix, @NonNull String interactionId) {
		return buttonIdPrefix + BUTTON_ID_SEPARATOR + interactionId;
	}

	/**
	 * Splits the given button id into its components
	 *
	 * @return array containing {@code [buttonIdPrefix, interactionId]}
	 * @see #buildButtonId(String, String)
	 */
	public static String[] splitButtonId(@NonNull ButtonInteractionEvent buttonInteractionEvent) {
		//noinspection DataFlowIssue interactable buttons always have an id
		return buttonInteractionEvent.getButton().getCustomId().split(BUTTON_ID_SEPARATOR);
	}
}
