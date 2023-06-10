package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.annotations.EventFieldDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.atteo.classindex.ClassIndex;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alf
 * @since 10.05.2021
 */
@SuppressWarnings("unchecked") //Suppressing cast to List<EventFieldDefaultDto>
@UtilityClass
@Slf4j
public final class EventFieldUtils {
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
	 * Returns the default {@link EventFieldDefaultDto}s for the given {@link EventType#getName()}
	 *
	 * @param eventTypeName name of event type
	 * @return matching default fields
	 */
	public static List<EventFieldDefaultDto> getDefault(String eventTypeName) {
		List<EventFieldDefaultDto> fieldDefaultsList = eventTypeNameToFieldDefaults.get(eventTypeName);
		return fieldDefaultsList == null ? Collections.emptyList() : fieldDefaultsList;
	}
}
