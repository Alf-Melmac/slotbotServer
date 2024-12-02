package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Repository
@Deprecated
public interface EventDetailsDefaultRepository extends SuperIdEntityJpaRepository<EventDetailsDefault> {
	Optional<EventDetailsDefault> findByEventTypeNameAndGuild(String eventTypeName, Guild guild);
}
