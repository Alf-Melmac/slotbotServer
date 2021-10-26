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
	public static boolean isEmpty(String term) {
		return !isNotEmpty(term);
	}

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
	 * Checks if the given string only contains numbers
	 * Fastet implementation according to https://stackoverflow.com/questions/10575624/java-string-see-if-a-string-contains-only-numbers-and-not-letters
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
	 * Gets the first string that is not empty.
	 *
	 * @param fallback to use if all given strings are empty
	 * @param strs strings that may be null or empty
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
}
