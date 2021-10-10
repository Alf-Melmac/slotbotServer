package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
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
public interface EventRepository extends SuperIdEntityJpaRepository<Event> {
	@Query("SELECT e.ownerGuild FROM Event e WHERE e.id = :id")
	long findGuildById(long id);


	List<Event> findAllByOwnerGuildAndDateTimeBetween(long ownerGuild, LocalDateTime start, LocalDateTime end);

	List<Event> findAllByOwnerGuildAndDateTimeBetweenAndHiddenFalse(long ownerGuild, LocalDateTime start, LocalDateTime end);

	@Query("SELECT e FROM Event e WHERE e.dateTime < :dateTime ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsBeforeAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime);

	@Query("SELECT e FROM Event e WHERE e.dateTime > :dateTime")
	List<Event> findAllByDateTimeIsAfter(@Param("dateTime") LocalDateTime dateTime);

	@Query(value = "SELECT e FROM Event e WHERE e.dateTime > :dateTime AND e.ownerGuild = :ownerGuild AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild = :ownerGuild) ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndOwnerGuildAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime, @Param("ownerGuild") long guildId);

	@Query(value = "SELECT e FROM Event e WHERE e.dateTime > :dateTime AND e.ownerGuild <> :ownerGuild AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild = :ownerGuild) ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndNotOwnerGuildAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime, @Param("ownerGuild") long guildId);

	@Query("SELECT s.user FROM Slot s WHERE s.user.id <> de.webalf.slotbot.model.User.DEFAULT_USER_ID AND EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.channel = :channel AND di.event = s.squad.event)")
	List<User> findAllParticipants(@Param("channel") long channel);
}
