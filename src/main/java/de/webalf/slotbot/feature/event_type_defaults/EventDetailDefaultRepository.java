package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailDefault;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Alf
 * @since 04.08.2024
 */
@Repository
interface EventDetailDefaultRepository extends SuperIdEntityJpaRepository<EventDetailDefault> {
	List<EventDetailDefault> findAllByEventType(EventType eventType);

	@Modifying
	//This is the exact same as the extraction of the method. No idea why a query is required
	@Query("DELETE FROM EventDetailDefault d WHERE d.eventType = :eventType AND d.id not in :ids")
	void deleteByEventTypeAndIdNotIn(@Param("eventType") EventType eventType, @Param("ids") Set<Long> ids);
}
