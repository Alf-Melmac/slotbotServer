package de.webalf.slotbot.repository;

import de.webalf.slotbot.feature.event_type_defaults.EventInfo;
import de.webalf.slotbot.feature.event_type_defaults.EventInfoWithId;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Repository
public interface EventRepository extends SuperIdEntityJpaRepository<Event> {
	@Query("SELECT e.ownerGuild FROM Event e WHERE e.id = :id")
	Optional<Guild> findOwnerGuildById(long id);

	@Query("SELECT e FROM Event e WHERE e.dateTime BETWEEN :start AND :end AND (e.shareable = true OR e.ownerGuild.id = de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER)")
	List<Event> findAllByDateTimeBetweenAndShareableTrueOrPlaceholderGuild(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("SELECT e FROM Event e WHERE e.dateTime BETWEEN :start AND :end AND e.hidden = false AND (e.shareable = true OR e.ownerGuild.id = de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER)")
	List<Event> findAllByDateTimeBetweenAndHiddenFalseAndShareableTrueOrPlaceholderGuild(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

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
			"WHERE e.dateTime BETWEEN :start AND :end AND e.hidden = false " +
			"AND (" +
			"e.ownerGuild = :guild " +
			"OR EXISTS(SELECT di FROM e.discordInformation di WHERE di.guild = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor = :guild))" +
			")")
	List<Event> findAllByGuildAndDateTimeBetweenAndHiddenFalse(@Param("guild") Guild ownerGuild, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

	@Query("SELECT e FROM Event e WHERE (e.ownerGuild = :ownerGuild OR EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild = :ownerGuild)) AND e.hidden = false")
	List<Event> findAllByGuildAndHiddenFalse(@Param("ownerGuild") Guild ownerGuild);

	@Query("SELECT e " +
			"FROM Event e " +
			"WHERE e.dateTime < :dateTime " +
			"AND (" +
			"e.ownerGuild = :guild " +
			"OR EXISTS(SELECT di FROM e.discordInformation di WHERE di.guild = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor = :guild) " +
			"OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor = :guild))" +
			")" +
			"ORDER BY e.dateTime")
	List<Event> findAllByDateTimeIsBeforeAndOwnerGuildAndOrderByDateTime(@Param("dateTime") LocalDateTime dateTime, @Param("guild") Guild guild);

	List<Event> findByDateTimeGreaterThan(LocalDateTime dateTime);


	@Query("""
			SELECT e
			FROM Event e
			WHERE e.dateTime > :dateTime
			AND e.ownerGuild.id = :ownerGuild
			AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild.id = :ownerGuild)""")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndOwnerGuildAndForGuild(@Param("dateTime") LocalDateTime dateTime, @Param("ownerGuild") long guildId, Pageable pageable);

	@Query("""
			SELECT e
			FROM Event e
			WHERE e.dateTime > :dateTime
			AND e.ownerGuild.id <> :guild
			AND NOT EXISTS(SELECT di FROM EventDiscordInformation di WHERE di.event = e AND di.guild.id = :guild)
			AND (
			e.shareable = true
			OR EXISTS(SELECT sq FROM e.squadList sq WHERE sq.reservedFor.id = :guild)
			OR EXISTS(SELECT sq FROM e.squadList sq WHERE EXISTS(SELECT sl FROM sq.slotList sl WHERE sl.reservedFor.id = :guild))
			)""")
	List<Event> findAllByDateTimeIsAfterAndNotScheduledAndNotOwnerGuildAndForGuild(@Param("dateTime") LocalDateTime dateTime, @Param("guild") long guildId, Pageable pageable);

	@Query("""
			SELECT s.user.id FROM Slot s INNER JOIN s.squad.event.discordInformation discordInformation
			WHERE discordInformation.channel = :channel AND s.user.id <> de.webalf.slotbot.model.User.DEFAULT_USER_ID
			""")
	List<Long> findAllParticipantIds(@Param("channel") long channel);

	Optional<Event> findFirstByOwnerGuildAndSquadListSlotListUserOrderByDateTimeDesc(Guild ownerGuild, User user);

	List<Event> findBySquadListSlotListUser(User user);

	Optional<Event> findFirstBySquadList_SlotList_UserAndDateTimeBeforeOrderByDateTimeDesc(User user, LocalDateTime dateTime);

	List<EventInfo> findDistinctByEventType(EventType eventType);

	List<EventInfoWithId> findByEventTypeIn(Collection<EventType> eventTypes);
}
