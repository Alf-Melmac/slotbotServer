package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern QUOTES = Pattern.compile("(\"[^\"]*\"|'[^']*'|[^\"' ]+)");

	public static List<String> splitOnSpacesExceptQuotes(String str) {
		final Matcher m = QUOTES.matcher(str);
		List<String> s = new ArrayList<>();
		while (m.find()) {
			String group = m.group();
			//Remove quotes if present
			if (group.startsWith("\"") && group.endsWith("\"") || group.startsWith("'") && group.endsWith("'")) {
				group = group.substring(1, group.length() - 1);
			}
			s.add(group);
		}
		return s;
	}

	/**
	 * Checks if the given string only contains numbers.
	 * Fastest implementation according to <a href="https://stackoverflow.com/questions/10575624/java-string-see-if-a-string-contains-only-numbers-and-not-letters">StackOverflow</a>
	 *
	 * @param str to check
	 * @return true if only number
	 */
	public static boolean onlyNumbers(String str) {
		for (int j = 0; j < str.length(); j++) {
			char charAt = str.charAt(j);
			if (!('0' <= charAt && charAt <= '9')) {
				return false;
			}
		}
		return true;
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
	 * Gets the first string that is not empty.
	 *
	 * @param fallback to use if all given strings are empty
	 * @param strs     strings that may be null or empty
	 * @return first found not empty string or the given fallback if non is not empty
	 */
	public static String getFirstNotEmpty(String fallback, String... strs) {
		for (String str : strs) {
			if (isNotEmpty(str)) {
				return str;
			}
		}
		return fallback;
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

	/**
	 * Prepends the prefix to the start of the string if the string does not already start with the prefix.
	 *
	 * @param s      to check
	 * @param prefix with which the string should start
	 * @return prefixed string
	 * @see org.apache.commons.lang3.StringUtils#prependIfMissing(String, CharSequence, CharSequence...)
	 */
	public static String prependIfMissing(String s, String prefix) {
		return org.apache.commons.lang3.StringUtils.prependIfMissing(s, prefix);
	}
}
