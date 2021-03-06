package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	Optional<Event> findByChannel(long channel);

	List<Event> findAllByDateTimeBetween(LocalDateTime start, LocalDateTime end);

	List<Event> findAllByDateTimeBetweenAndHiddenFalse(LocalDateTime start, LocalDateTime end);
}
