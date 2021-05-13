package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.OldEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 14.05.2021
 */
@Repository
public interface OldEventRepository extends JpaRepository<OldEvent, Long> {
}
