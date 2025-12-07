package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class to work with {@link Long}s
 *
 * @author Alf
 * @since 08.08.2020
 */
@UtilityClass
public final class LongUtils {
	/**
	 * Parse Long that can handle null strings
	 *
	 * @param s string to parse
	 * @return string converted to long or 0 if string was null
	 */
	public static long parseLong(String s) {
		return parseLong(s, 0);
	}

	/**
	 * Parse Long that can handle null strings. Returns {@code fallback} if null
	 *
	 * @param s        string to parse
	 * @param fallback to use if s is null
	 * @return string converted to long or fallback if string was null
	 */
	public static long parseLong(String s, long fallback) {
		return s != null ? Long.parseLong(s) : fallback;
	}

	public static Long parseLongWrapper(String s) {
		return s != null ? Long.parseLong(s) : null;
	}

	public static String toString(Long l) {
		return l != null ? Long.toString(l) : null;
	}
}
