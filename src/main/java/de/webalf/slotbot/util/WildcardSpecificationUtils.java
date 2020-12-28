package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 22.12.2020
 */
@UtilityClass
public final class WildcardSpecificationUtils {
	private static final String WILDCARD = "%";
	private static final String SINGLE_CHAR_WILDCARD = "_";

	public static String buildLowerCaseWildcardParam(@NonNull String term) {
		return WILDCARD + escapeWildcardChars(term.trim().toLowerCase()) + WILDCARD;
	}

	private static String escapeWildcardChars(String term) {
		return StringUtils.isNotEmpty(term)
				? term
				.replaceAll(WILDCARD, "\\\\" + WILDCARD)
				.replaceAll(SINGLE_CHAR_WILDCARD, "\\\\" + SINGLE_CHAR_WILDCARD)
				: term;
	}
}
