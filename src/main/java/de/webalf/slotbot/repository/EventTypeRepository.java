package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
	Optional<EventType> findEventTypeByNameAndColor(String name, String color);
}
