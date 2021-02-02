package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * @author Alf
 * @since 18.01.2021
 */
@UtilityClass
public final class DateUtils {
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.uuuu");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static Comparator<LocalDateTime> getLocalDateTimeComparator() {
		return (dateTime1, dateTime2) -> {
			final int before = dateTime1.isBefore(dateTime2) ? 1 : -1;
			return dateTime1.isEqual(dateTime2) ? 0 : before;
		};
	}
}
