package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * Util class to work with {@link Collection}s
 *
 * @author Alf
 * @since 28.12.2020
 */
@UtilityClass
public final class CollectionUtils {
	public static boolean isNotEmpty(Collection<?> collection) {
		return !org.springframework.util.CollectionUtils.isEmpty(collection);
	}
}
