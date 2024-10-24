package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SlotRepository extends SuperIdEntityJpaRepository<Slot> {
	long countByUserAndSquadEventDateTimeBefore(User user, LocalDateTime dateTime);

	Optional<Slot> findByNumberAndSquadEvent(int number, Event event);

	Optional<Slot> findByUserAndSquadEvent(User user, Event event);

	@Query("""
			SELECT s
			FROM Slot s
			WHERE s.user = :user
			AND (s.squad.event.ownerGuild = :guild OR s.reservedFor = :guild OR (s.reservedFor IS NULL AND s.squad.reservedFor = :guild))
			AND s.squad.event.dateTime > :dateTime
			""")
	List<Slot> findByUserAndForGuildAndEventAfter(User user, Guild guild, LocalDateTime dateTime);

	@Query("""
			SELECT s
			FROM Slot s
			WHERE s.user = :user
			AND s.squad.event.dateTime > :dateTime
			""")
	List<Slot> findByUserAndEventAfter(User user, LocalDateTime dateTime);
}
