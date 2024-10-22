package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Ban;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 13.10.24
 */
@Repository
public interface BanRepository extends SuperIdEntityJpaRepository<Ban> {
	@Query("""
			SELECT (COUNT(b) > 0) FROM Ban b
			WHERE (b.user = :user AND b.guild in :guilds) OR b.user = :user""")
	boolean existsByUserAndGuildInOrUser(@Param("user") User user, @Param("guilds") Guild[] guilds);

	boolean existsByUserAndGuildNull(User user);
}
