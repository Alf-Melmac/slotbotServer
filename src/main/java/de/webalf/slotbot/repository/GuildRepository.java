package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Guild;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 04.01.2022
 */
public interface GuildRepository extends DiscordIdEntityJpaRepository<Guild> {
	Optional<Guild> findByGroupIdentifier(String name);

	List<Guild> findAllByOrderByGroupIdentifier();

	@Query("SELECT g.advanced FROM Guild g WHERE g.groupIdentifier = :identifier")
	boolean isAdvancedByIdentifier(@Param("identifier") String identifier);

	@Query("""
			SELECT COUNT(g) > 0 FROM Guild g
			WHERE g.id = :id AND (g.memberRole IN :roles OR g.eventManageRole IN :roles OR g.adminRole IN :roles)""")
	boolean existsByIdAndAnyRoleIn(@Param("id") long id, @Param("roles") Collection<Long> roles);
}