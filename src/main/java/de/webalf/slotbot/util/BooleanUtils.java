package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 24.11.2020
 */
@UtilityClass
public final class BooleanUtils {
	/**
	 * Returns false if the given Boolean is null
	 *
	 * @param bool Boolean that can be null
	 * @return bool value or false
	 */
	public static boolean falseIfNull(Boolean bool) {
		return defaultIfNull(bool, false);
	}

	/**
	 * Returns the given default value if the given Boolean is null
	 *
	 * @param bool Boolean that can be null
	 * @param def default value
	 * @return bool value or default value
	 */
	private static boolean defaultIfNull(Boolean bool, boolean def) {
		return bool == null ? def : bool;
	}
}
