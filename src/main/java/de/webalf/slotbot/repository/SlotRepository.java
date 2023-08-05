package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SlotRepository extends SuperIdEntityJpaRepository<Slot> {
	long countByUserAndSquadEventDateTimeBefore(User user, LocalDateTime dateTime);

	Optional<Slot> findByNumberAndSquadEvent(int number, Event event);
}
