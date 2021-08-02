package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventField;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Repository
public interface EventFieldRepository extends SuperIdEntityJpaRepository<EventField> {
}
