package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventType;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.transaction.NotSupportedException;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Repository
public interface EventTypeRepository extends SuperIdEntityJpaRepository<EventType> {
	Optional<EventType> findEventTypeByNameAndColor(String name, String color);

	List<EventType> findAllByOrderByName();

	/**
	 * @see #findEventTypeByNameAndColor(String, String)
	 */
	@SneakyThrows(NotSupportedException.class)
	@Override
	default @NotNull Optional<EventType> findById(@NotNull Long l) {
		throw new NotSupportedException("Id shouldn't be used to get entity. Use findEventTypeByNameAndColor");
	}

	@SneakyThrows(NotSupportedException.class)
	@Override
	default @NotNull List<EventType> findAllById(@NotNull Iterable<Long> iterable) {
		throw new NotSupportedException("Id shouldn't be used to get entity.");
	}
}
