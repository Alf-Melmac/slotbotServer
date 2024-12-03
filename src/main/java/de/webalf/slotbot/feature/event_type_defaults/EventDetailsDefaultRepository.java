package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailsDefault;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Repository
@Deprecated
public interface EventDetailsDefaultRepository extends SuperIdEntityJpaRepository<EventDetailsDefault> {
}
