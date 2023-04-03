package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SlotRepository extends SuperIdEntityJpaRepository<Slot> {
	long countByUserAndSquadEventDateTimeBefore(User user, LocalDateTime dateTime);
}
