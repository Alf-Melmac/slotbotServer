package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 21.10.2021
 */
@UtilityClass
public final class BooleanUtils {
	/**
	 * Parse Boolean that can handle null bools. Returns {@code fallback} if null
	 *
	 * @param b        Boolean to parse
	 * @param fallback to use if b is null
	 * @return Boolean converted to boolean or fallback if Boolean was null
	 */
	public static boolean parseBoolean(Boolean b, boolean fallback) {
		return b != null ? b : fallback;
	}
}
