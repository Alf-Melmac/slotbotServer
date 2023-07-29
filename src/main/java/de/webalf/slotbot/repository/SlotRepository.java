package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SlotRepository extends SuperIdEntityJpaRepository<Slot> {
	long countByUserAndSquadEventDateTimeBefore(User user, LocalDateTime dateTime);

	@Modifying
	@Query("UPDATE Slot s SET s.name = :name WHERE s.number = :number AND s.squad IN (SELECT sq FROM Squad sq WHERE s.squad = sq AND sq.event = :event)")
	void updateNameByEventAndNumber(@Param("name") String newSlotName, @Param("event") Event event, @Param("number") int slotNumber);
}
