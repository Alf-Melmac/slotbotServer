package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.Guild;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Repository
public interface EventDetailsDefaultRepository extends SuperIdEntityJpaRepository<EventDetailsDefault> {
	Optional<EventDetailsDefault> findByEventTypeNameAndGuild(String eventTypeName, Guild guild);

	void deleteByEventTypeNameAndGuild(String eventTypeName, Guild guild);
}
