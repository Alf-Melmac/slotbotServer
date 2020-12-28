package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 22.12.2020
 */
@UtilityClass
public final class StringUtils {
	public static boolean isNotEmpty(String term) {
		return !org.springframework.util.StringUtils.isEmpty(term);
	}
}
