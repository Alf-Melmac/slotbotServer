package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.User;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Repository
public interface UserRepository extends DiscordIdEntityJpaRepository<User> {
}
