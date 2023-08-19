package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 22.12.2020
 */
@UtilityClass
public final class StringUtils {
	/**
	 * Checks whether the given string contains no text.
	 *
	 * @param term to check
	 * @return true if the string contains no text
	 * @see #isNotEmpty(String)
	 */
	public static boolean isEmpty(String term) {
		return !isNotEmpty(term);
	}

	/**
	 * Checks whether the given string contains text.
	 *
	 * @param term to check
	 * @return true if the string contains text
	 * @see org.springframework.util.StringUtils#hasText(String)
	 * @see #isEmpty(String)
	 */
	public static boolean isNotEmpty(String term) {
		return org.springframework.util.StringUtils.hasText(term);
	}

	/**
	 * Splits a string into substrings of length n and returns them as a list.
	 *
	 * @param str the string to split
	 * @param n   the length of each substring
	 * @return a list of substrings of length n
	 */
	public static List<String> splitEveryNth(@NonNull String str, int n) {
		final List<String> split = new ArrayList<>();
		for (int i = 0; i < str.length(); i += n) {
			split.add(str.substring(i, Math.min(str.length(), i + n)));
		}
		return split;
	}

	private static final String NON_DIGIT_REGEX = "\\D";

	public static String removeNonDigitCharacters(String str) {
		return str.replaceAll(NON_DIGIT_REGEX, "");
	}

	/**
	 * Returns a trimmed version of the given string. The string may be null.
	 *
	 * @param str string to trim
	 * @return trimmed version of the given string
	 */
	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	/**
	 * Returns a trimmed version of the given string. If the string is null or empty, null is returned.
	 *
	 * @param str string to trim
	 * @return trimmed string
	 */
	public static String trimAndNullify(String str) {
		final String trimmed = trim(str);
		return isEmpty(trimmed) ? null : trimmed;
	}

	/**
	 * Removes the prefix if the string starts with it.
	 *
	 * @param s      to check
	 * @param prefix to remove
	 * @return string without prefix
	 */
	public static String stripPrefixIfExists(String s, String prefix) {
		if (s == null) {
			return null;
		}
		if (s.startsWith(prefix)) {
			return s.substring(prefix.length());
		}
		return s;
	}
}
