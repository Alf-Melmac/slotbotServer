package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 28.11.2024
 */
@Repository
interface RequirementRepository extends SuperIdEntityJpaRepository<Requirement> {
}
