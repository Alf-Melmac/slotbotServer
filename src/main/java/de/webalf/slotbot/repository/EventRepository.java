package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	List<Event> findAllByDateTimeBetween(LocalDateTime start, LocalDateTime end);

	List<Event> findAllByDateTimeBetweenAndHiddenFalse(LocalDateTime start, LocalDateTime end);

	@Query("SELECT e FROM Event e WHERE e.dateTime < :dateTime ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsBeforeAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime);

	@Query(value = "SELECT e FROM Event e WHERE e.dateTime > :dateTime AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e) ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsAfterAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime);

	@Query("SELECT s.user FROM Slot s WHERE s.squad.event.discordInformation.channel = :channel AND s.user.id <> de.webalf.slotbot.model.User.DEFAULT_USER_ID")
	List<User> findAllParticipants(@Param("channel") long channel);
}
