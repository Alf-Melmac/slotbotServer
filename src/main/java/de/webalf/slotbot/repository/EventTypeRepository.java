package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Repository
public interface EventTypeRepository extends SuperIdEntityJpaRepository<EventType> {
	Optional<EventType> findByNameAndColorAndGuild(String name, String color, Guild guild);

	List<EventType> findByGuildNullOrGuildOrderByName(Guild guild);

	Optional<EventType> findByNameAndGuild(String name, Guild guild);

	List<EventType> findByGuildNull();
}
