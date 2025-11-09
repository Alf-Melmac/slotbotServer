package de.webalf.slotbot.util;

import de.webalf.slotbot.model.AbstractDiscordIdEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

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

	/**
	 * Calls the consumer with the given value if the value is not null. Empty strings are presented but are treated as null.
	 */
	public static void ifPresentOrEmpty(String value, Consumer<String> consumer) {
		if (isPresent((Object) value)) {
			consumer.accept(value.isEmpty() ? null : value);
		}
	}

	public static void ifPresent(int value, IntConsumer consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(Boolean value, Consumer<Boolean> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	public static void ifPresent(LocalDateTime value, Consumer<LocalDateTime> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <T> void ifPresent(Optional<T> value, Consumer<T> consumer) {
		if (isPresent(value)) {
			consumer.accept(value.orElse(null));
		}
	}

	public static <T> void ifPresentObject(T value, Consumer<T> consumer) {
		if (isPresent(value)) {
			consumer.accept(value);
		}
	}

	/**
	 * Returns the id of the given entity if it is not null, otherwise null
	 *
	 * @param entity to get id from
	 * @param <T>    type of entity
	 * @return id of entity or null
	 */
	private static <T extends AbstractDiscordIdEntity> Long getIdIfPresent(T entity) {
		return entity != null ? entity.getId() : null;
	}

	/**
	 * Returns the id of the given entity as string if it is not null, otherwise null
	 *
	 * @param entity to get id from
	 * @param <T>    type of entity
	 * @return id of entity or null
	 */
	public static <T extends AbstractDiscordIdEntity> String getIdStringIfPresent(T entity) {
		return LongUtils.toString(getIdIfPresent(entity));
	}
}
