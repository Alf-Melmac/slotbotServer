package de.webalf.slotbot.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alf
 * @since 09.10.2025
 */
class DateUtilsTest {
	@ParameterizedTest(name = "{0}")
	@MethodSource
	void isMoreThan24HoursAgo(String name, LocalDateTime input, boolean expected) {
		assertThat(DateUtils.isMoreThan24HoursAgo(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> isMoreThan24HoursAgo() {
		final LocalDateTime now = DateUtils.now();
		return Stream.of(
				Arguments.of("1 month ago", now.minusMonths(1), true),
				Arguments.of("2 days ago", now.minusDays(2), true),
				Arguments.of("more than 1 day ago", now.minusDays(1).minusMinutes(1), true),
				Arguments.of("1 day ago", now.minusDays(1), true),
				Arguments.of("close to 1 day ago", now.minusHours(23).minusMinutes(59), false),
				Arguments.of("12 hours ago", now.minusHours(12), false),
				Arguments.of("now", now, false),
				Arguments.of("tomorrow", now.plusDays(1), false),
				Arguments.of("next month", now.plusMonths(1), false)
		);
	}
}
