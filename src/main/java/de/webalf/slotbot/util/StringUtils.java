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
	public static boolean isNotEmpty(String term) {
		return !org.springframework.util.StringUtils.isEmpty(term);
	}

	private static final String REGEX = "(\"[^\"]*\"|'[^']*'|[^\"' ]+)";

	public static List<String> splitOnSpacesExceptQuotes(String str) {
		final Matcher m = Pattern.compile(REGEX).matcher(str);
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
}
