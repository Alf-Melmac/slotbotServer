package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Alf
 * @since 18.01.2021
 */
@UtilityClass
public final class DateUtils {
	/**
	 * Checks if the given date time is more than 24 hours ago
	 *
	 * @param dateTime in utc to check
	 */
	public static boolean isMoreThan24HoursAgo(@NonNull LocalDateTime dateTime) {
		return Instant.now().minus(1, DAYS).isAfter(dateTime.toInstant(UTC));
	}

	/**
	 * Returns the current date time in utc offset
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now(UTC);
	}

	public static LocalDateTime atEndOfDay(@NonNull LocalDate date) {
		return date.atTime(23, 59, 59);
	}
}
