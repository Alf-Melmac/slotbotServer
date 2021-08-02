package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Slot;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SlotRepository extends IdEntityJpaRepository<Slot> {
}
