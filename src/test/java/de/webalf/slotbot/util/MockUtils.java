package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.mockito.Mockito.when;

/**
 * @author Alf
 * @since 26.04.2023
 */
@UtilityClass
public final class MockUtils {
	public static final Locale TEST_LOCALE = Locale.GERMAN;

	public static void mockMessage(MessageSource messageSourceMock, String result, String key, String... args) {
		if (args.length == 0) {
			args = null;
		}
		when(messageSourceMock.getMessage(key, args, TEST_LOCALE)).thenReturn(result);
	}
}
