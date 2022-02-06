package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
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
	Guild findOwnerGuildById(long id);

	@Query("SELECT e FROM Event e WHERE e.dateTime BETWEEN :start AND :end AND (e.shareable = true OR e.ownerGuild.id = de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER)")
	List<Event> findAllByDateTimeBetweenAndShareableTrueOrPlaceholderGuild(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT e FROM Event e WHERE e.dateTime BETWEEN :start AND :end AND e.hidden = false AND (e.shareable = true OR e.ownerGuild.id = de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER)")
	List<Event> findAllByDateTimeBetweenAndHiddenFalseAndShareableTrueOrPlaceholderGuild(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT e " +
			"FROM Event e " +
			"WHERE e.dateTime BETWEEN :start AND :end AND " +
			"(" +
			"e.ownerGuild = :guild " +
			"OR EXISTS(SELECT di FROM e.discordInformation di WHERE di.guild = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor = :guild))" +
			")")
	List<Event> findAllByGuildAndDateTimeBetween(@Param("guild") Guild ownerGuild, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT e " +
			"FROM Event e " +
			"WHERE e.dateTime BETWEEN :start AND :end AND e.hidden = false AND " +
			"(" +
			"e.ownerGuild = :guild " +
			"OR EXISTS(SELECT di FROM e.discordInformation di WHERE di.guild = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor = :guild))" +
			")")
	List<Event> findAllByGuildAndDateTimeBetweenAndHiddenFalse(@Param("guild") Guild ownerGuild, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT e FROM Event e WHERE (e.ownerGuild = :ownerGuild OR EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild = :ownerGuild)) AND e.hidden = false")
	List<Event> findAllByGuildAndHiddenFalse(@Param("ownerGuild") Guild ownerGuild);

	@Query("SELECT e FROM Event e WHERE e.dateTime < :dateTime ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsBeforeAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime);

	@Query("SELECT e FROM Event e WHERE e.dateTime > :dateTime")
	List<Event> findAllByDateTimeIsAfter(@Param("dateTime") LocalDateTime dateTime);

	@Query(value = "SELECT e " +
			"FROM Event e " +
			"WHERE e.dateTime > :dateTime " +
			"AND e.ownerGuild.id = :ownerGuild " +
			"AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild.id = :ownerGuild) " +
			"ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndOwnerGuildAndForGuildAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime, @Param("ownerGuild") long guildId);

	@Query(value = "SELECT e " +
			"FROM Event e " +
			"WHERE e.dateTime > :dateTime AND e.ownerGuild.id <> :guild AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild.id = :guild) " +
			"AND (" +
			"e.shareable = true " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor = :guild))" +
			") " +
			"ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndNotOwnerGuildAndForGuildAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime, @Param("guild") long guildId);

	@Query("SELECT s.user FROM Slot s WHERE s.user.id <> de.webalf.slotbot.model.User.DEFAULT_USER_ID AND EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.channel = :channel AND di.event = s.squad.event)")
	List<User> findAllParticipants(@Param("channel") long channel);
}
