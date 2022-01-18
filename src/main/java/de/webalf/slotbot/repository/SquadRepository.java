package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Squad;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Repository
public interface SquadRepository extends SuperIdEntityJpaRepository<Squad> {
}
