package de.webalf.slotbot.feature.action_log;

import de.webalf.slotbot.feature.action_log.model.ActionLog;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Repository
public interface ActionLogRepository extends SuperIdEntityJpaRepository<ActionLog> {
	List<ActionLog> findByActionObjectIdOrderByTimeGapAsc(long actionObjectId);

	void deleteByActionObjectId(long actionObjectId);
}
