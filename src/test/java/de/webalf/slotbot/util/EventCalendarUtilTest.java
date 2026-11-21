package de.webalf.slotbot.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 22.11.2026
 */
class EventCalendarUtilTest {
	@ParameterizedTest
	@MethodSource
	void isCalendarName(String filename, boolean expected) {
		assertEquals(expected, EventCalendarUtil.isCalendarName(filename));
	}

	private static Stream<Arguments> isCalendarName() {
		return Stream.of(
				Arguments.of("0.ics", true),
				Arguments.of("42.ics", true),
				Arguments.of("-1.ics", true),
				Arguments.of("706254758721224707.ics", true),
				Arguments.of("9223372036854775807.ics", true),
				Arguments.of("-9223372036854775808.ics", true),
				Arguments.of("Foo", false),
				Arguments.of("Foo.ics", false),
				Arguments.of("42", false),
				Arguments.of(".ics", false),
				Arguments.of("42.ICS", false),
				Arguments.of("42.ics.ics", false),
				Arguments.of("4.2.ics", false),
				Arguments.of(" 42.ics", false),
				Arguments.of("9223372036854775808.ics", false)
		);
	}
}
