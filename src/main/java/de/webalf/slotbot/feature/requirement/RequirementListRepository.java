package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alf
 * @since 15.11.2024
 */
@Repository
interface RequirementListRepository extends SuperIdEntityJpaRepository<RequirementList> {
	List<RequirementList> findByGuild_IdOrGuildNull(long id);
}
