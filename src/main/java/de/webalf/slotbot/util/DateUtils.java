package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

/**
 * @author Alf
 * @since 18.01.2021
 */
@UtilityClass
public final class DateUtils {
	/**
	 * Returns the given date time in utc offset
	 */
	public static ZonedDateTime getDateTimeZoned(@NonNull LocalDateTime dateTime) {
		return dateTime.atZone(UTC);
	}

	/**
	 * Checks if the given date time in utc offset if in the future
	 */
	public static boolean isInFuture(@NonNull LocalDateTime dateTime) {
		return Instant.now().isBefore(dateTime.toInstant(UTC));
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
