package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUsers;
import de.webalf.slotbot.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Repository
public interface GuildUsersRepository extends SuperIdEntityJpaRepository<GuildUsers> {
	List<GuildUsers> findByGuild(Guild guild);

	Optional<GuildUsers> findByGuildAndUser(Guild guild, User user);

	void deleteByGuildAndUser(Guild guild, User user);
}
