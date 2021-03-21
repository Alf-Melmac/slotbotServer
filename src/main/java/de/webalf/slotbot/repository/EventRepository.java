package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("SELECT s.user FROM Slot s WHERE s.squad.event.channel = :channel AND s.user.id <> de.webalf.slotbot.model.User.DEFAULT_USER_ID")
	List<User> findAllParticipants(@Param("channel") long channel);
}
