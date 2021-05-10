package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.enums.EventFieldType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.webalf.slotbot.model.enums.EventFieldType.SELECTION;
import static de.webalf.slotbot.model.enums.EventFieldType.TEXT_WITH_SELECTION;

/**
 * @author Alf
 * @since 10.05.2021
 */
@UtilityClass
public final class EventFieldUtils {
	/**
	 * Returns a link if the given {@link EventField} references something.
	 *
	 * @param eventField to build link for
	 * @return link if known or null
	 */
	public static String buildOptionalLink(EventField eventField) {
		final String title = eventField.getTitle();

		if ("Modpack".equalsIgnoreCase(title)) {
			return Arma3FieldUtils.getModPackUrl(eventField.getText());
		}

		return null;
	}

	/**
	 * Returns the default {@link EventField}s for the given {@link EventType#name}
	 *
	 * @param eventTypeName name of event type
	 * @return matching default fields (including only field titles)
	 */
	public static List<EventFieldDefaultDto> getDefault(String eventTypeName) {
		switch (eventTypeName) {
			case Arma3FieldUtils.EVENT_TYPE_NAME:
				return Arma3FieldUtils.FIELDS;
			default:
				return Collections.emptyList();
		}
	}

	/**
	 * Returns the default {@link EventField}s for the given {@link EventField}
	 * Uses the event type name of the event
	 *
	 * @see #getDefault(String)
	 */
	private static List<EventFieldDefaultDto> getDefault(@NonNull EventField eventField) {
		return getDefault(eventField.getEvent().getEventType().getName());
	}

	/**
	 * Returns the default {@link EventFieldType} if the given {@link EventField} is a default field
	 *
	 * @param eventField to get default fieldType for
	 * @return default field type or {@link EventFieldType#TEXT}
	 */
	public static EventFieldType getDefaultFieldType(EventField eventField) {
		return getOptionalMatchingDefaultField(eventField)
				.map(EventFieldDefaultDto::getType)
				.orElse(EventFieldType.TEXT);
	}

	public static List<String> getDefaultSelection(EventFieldType fieldType, EventField eventField) {
		if (fieldType != TEXT_WITH_SELECTION && fieldType != SELECTION) {
			return Collections.emptyList();
		}
		return getOptionalMatchingDefaultField(eventField)
				.map(EventFieldDefaultDto::getSelection)
				.orElse(Collections.emptyList());
	}

	/**
	 * Finds the {@link EventFieldDefaultDto} for the given {@link EventField} found by title if present
	 *
	 * @param eventField to find default for
	 * @return optional event field default
	 */
	private static Optional<EventFieldDefaultDto> getOptionalMatchingDefaultField(@NonNull EventField eventField) {
		final String title = eventField.getTitle();
		return getDefault(eventField).stream()
				.filter(field -> title.equalsIgnoreCase(field.getTitle()))
				.findAny(); //Event field default titles are unique
	}
}