package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Guild;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 04.01.2022
 */
public interface GuildRepository extends SuperIdEntityJpaRepository<Guild> {
	Optional<Guild> findByDiscordId(long discordId);

	Optional<Guild> findByGroupIdentifier(String name);

	List<Guild> findAllByOrderByGroupIdentifier();

	@Query("SELECT g.advanced FROM Guild g WHERE g.groupIdentifier = :identifier")
	Boolean isAdvancedByIdentifier(@Param("identifier") String identifier);

	@Query("""
			SELECT COUNT(g) > 0 FROM Guild g
			WHERE g.discordId = :discordId AND (g.memberRole IN :roles OR g.eventManageRole IN :roles OR g.adminRole IN :roles)""")
	boolean existsByDiscordIdAndAnyRoleIn(@Param("discordId") long discordId, @Param("roles") Collection<Long> roles);

	@NativeQuery("""
			SELECT DISTINCT ON (e.event_owner_guild) g.id, e.event_date
			FROM event e
			JOIN guild g ON g.id = e.event_owner_guild
			ORDER BY e.event_owner_guild, e.event_date DESC""")
	List<Object[]> findDistinctByOwnerGuildOrderByDateTimeDesc();
}
