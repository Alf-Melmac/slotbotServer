package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.*;
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

	private static final ZoneId ZONE_BERLIN = ZoneId.of("Europe/Berlin");

	public static ZonedDateTime getDateTimeZoned(@NonNull LocalDateTime dateTime) {
		return dateTime.atZone(ZONE_BERLIN);
	}

	public static ZonedDateTime getDateTimeNowZoned() {
		return ZonedDateTime.ofInstant(Instant.now(), ZONE_BERLIN);
	}

	public static ZonedDateTime getDateTimeZoned(@NonNull LocalDate date, @NonNull LocalTime time) {
		return date.atTime(time).atZone(ZONE_BERLIN);
	}
}
