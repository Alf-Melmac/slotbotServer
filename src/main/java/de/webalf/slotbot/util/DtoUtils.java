package de.webalf.slotbot.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

/**
 * @author Alf
 * @since 08.08.2020
 */
public class DtoUtils {

	private static boolean isPresent(int value) {
		return value != 0;
	}

	private static boolean isPresent(String value) {
		return value != null && !StringUtils.isEmpty(value);
	}

	private static boolean isPresent(Object value) {
		return value != null;
	}

	public static void ifPresent(String value, Consumer<String> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(int value, Consumer<Integer> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(LocalDate value, Consumer<LocalDate> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(LocalTime value, Consumer<LocalTime> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}
}
