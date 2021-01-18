package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * @author Alf
 * @since 18.01.2021
 */
@UtilityClass
public final class DateUtils {
	public static Comparator<LocalDateTime> getLocalDateTimeComparator() {
		return (dateTime1, dateTime2) -> {
			final int before = dateTime1.isBefore(dateTime2) ? 1 : -1;
			return dateTime1.isEqual(dateTime2) ? 0 : before;
		};
	}
}
