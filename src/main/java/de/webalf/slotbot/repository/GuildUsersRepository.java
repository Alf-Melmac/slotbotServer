package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.GuildUserId;
import de.webalf.slotbot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Repository
public interface GuildUsersRepository extends JpaRepository<GuildUser, GuildUserId> {
	Page<GuildUser> findByGuild(Guild guild, Pageable pageable);

	Set<GuildUser> findByIdUserId(long userId);

	Optional<GuildUser> findByGuildAndUser(Guild guild, User user);

	void deleteByGuildAndUser(Guild guild, User user);
}
