package de.webalf.slotbot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 30.07.2021
 */
@UtilityClass
public final class AssertionUtils {
	public static void assertMessageEquals(String expected, @NonNull Throwable throwable) {
		assertEquals(expected, throwable.getMessage());
	}
}
