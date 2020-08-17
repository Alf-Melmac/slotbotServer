package de.webalf.slotbot.util;

/**
 * @author Alf
 * @since 08.08.2020
 */
public class LongUtils {
	/**
	 * Parse Long that can handle null strings
	 *
	 * @param s string to parse
	 * @return string converted to long or 0 if string was null
	 */
	public static long parseLong(String s) {
		return s != null ? Long.parseLong(s) : 0;
	}
}
