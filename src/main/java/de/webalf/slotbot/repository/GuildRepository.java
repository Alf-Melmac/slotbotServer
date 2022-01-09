package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Guild;

import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 04.01.2022
 */
public interface GuildRepository extends DiscordIdEntityJpaRepository<Guild> {
	Optional<Guild> findByGroupIdentifier(String name);

	List<Guild> findByUrlPatternIsNotNull();
}