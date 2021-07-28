package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.annotations.EventFieldDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.enums.EventFieldType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.atteo.classindex.ClassIndex;

import java.util.*;

import static de.webalf.slotbot.model.enums.EventFieldType.SELECTION;
import static de.webalf.slotbot.model.enums.EventFieldType.TEXT_WITH_SELECTION;

/**
 * @author Alf
 * @since 10.05.2021
 */
@SuppressWarnings("unchecked") //Suppressing cast to List<EventFieldDefaultDto>
@UtilityClass
@Slf4j
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

	private static final Map<String, List<EventFieldDefaultDto>> eventTypeNameToFieldDefaults = new HashMap<>();
	static {
		ClassIndex.getAnnotated(EventFieldDefault.class)
				.forEach(aClass -> {
					try {
						eventTypeNameToFieldDefaults.put(
								aClass.getAnnotation(EventFieldDefault.class).eventTypeName(),
								(List<EventFieldDefaultDto>) aClass.getDeclaredField("FIELDS").get(null)
						);
					} catch (NoSuchFieldException | IllegalAccessException e) {
						log.error("Wrong implementation of EventFieldDefault: " + aClass.getName(), e);
					}
				});
	}

	/**
	 * Returns the default {@link EventFieldDefaultDto}s for the given {@link EventType#name}
	 *
	 * @param eventTypeName name of event type
	 * @return matching default fields
	 */
	public static List<EventFieldDefaultDto> getDefault(String eventTypeName) {
		return eventTypeNameToFieldDefaults.get(eventTypeName);
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
		final List<EventFieldDefaultDto> defaultFields = getDefault(eventField);
		if (defaultFields == null) {
			return Optional.empty();
		}
		return defaultFields.stream()
				.filter(field -> title.equalsIgnoreCase(field.getTitle()))
				.findAny(); //Event field default titles are unique
	}
}
