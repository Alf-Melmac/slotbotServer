package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.ActionLog;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Repository
public interface ActionLogRepository extends SuperIdEntityJpaRepository<ActionLog> {
	void deleteByActionObjectId(long actionObjectId);
}
