package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Utility class to work with {@link List}s
 *
 * @author Alf
 * @since 14.11.2020
 */
@UtilityClass
public final class ListUtils {
	/**
	 * Removes the first element from the list and returns that removed element.
	 *
	 * @param list to shift
	 * @return first now removed element
	 */
	public static <T> T shift(@NonNull List<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		return list.remove(0);
	}
}
