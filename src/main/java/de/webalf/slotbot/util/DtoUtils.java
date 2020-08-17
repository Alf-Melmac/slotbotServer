package de.webalf.slotbot.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

/**
 * @author Alf
 * @since 08.08.2020
 */
public class DtoUtils {
	//TODO
	private static final String DEFAULT_STRING = "<NULL>";
	private static final LocalDate DEFAULT_DATE = LocalDate.MIN;
	private static final LocalTime DEFAULT_TIME = LocalTime.MIN;

	/*public static void ifPresent(? value, Consumer<?> consumer) {
		if (value != null) {
			consumer.accept(value);
		}
	}*/

	private static boolean isPresent(int value) {
		return value != 0;
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

	/*public static boolean isPresent(LocalDate value) {
		return !DEFAULT_DATE.equals(value);
	}*/

	public static void ifPresent(LocalDate value, Consumer<LocalDate> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	/*public static boolean isPresent(LocalTime value) {
		return !DEFAULT_TIME.equals(value);
	}*/

	public static void ifPresent(LocalTime value, Consumer<LocalTime> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	/*public static void ifPresent(List<SquadDto> value, Consumer<List<SquadDto>> consumer) {
		if (value != null && !value.isEmpty()) {
			consumer.accept(value);
		}
	}*/
}
