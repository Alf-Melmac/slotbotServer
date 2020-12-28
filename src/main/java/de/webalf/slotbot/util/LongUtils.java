package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

/**
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
		return s != null ? Long.parseLong(s) : 0;
	}

	public static Long parseLongWrapper(String s) {
		return s != null ? Long.parseLong(s) : null;
	}

	/**
	 * Returns the next whole long for the given double in a string
	 *
	 * @param s string including a double
	 * @return rounded up long from the given double in a string
	 */
	public static long parseCeilLongFromDoubleString(String s) {
		return (long) Math.ceil(Double.parseDouble(s));
	}

	public static String toString(Long l) {
		return l != null ? Long.toString(l) : null;
	}
}
