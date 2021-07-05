package de.webalf.slotbot.util;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

/**
 * @author Alf
 * @since 08.08.2020
 */
@UtilityClass
public final class DtoUtils {

	private static boolean isPresent(int value) {
		return value != 0;
	}

	private static boolean isPresent(String value) {
		return StringUtils.isNotEmpty(value);
	}

	private static boolean isPresent(Object value) {
		return value != null;
	}

	public static void ifPresent(String value, Consumer<String> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresentOrEmpty(String value, Consumer<String> consumer) {
		if (isPresent((Object) value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresentParse(String value, Consumer<Long> consumer) {
		if (isPresent(value)) {
			consumer.accept(LongUtils.parseLong(value));
		}
	}

	public static void ifPresent(int value, Consumer<Integer> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(Boolean value, Consumer<Boolean> consumer) {
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

	public static void ifPresent(UserDto value, Consumer<User> consumer) {
		if (isPresent(value)) {
			consumer.accept(UserAssembler.fromDto(value));
		}
	}

	public static <T> void ifPresentObject(T value, Consumer<T> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}
}
