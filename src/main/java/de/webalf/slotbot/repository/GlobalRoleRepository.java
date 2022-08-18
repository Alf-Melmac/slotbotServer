package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.GlobalRole;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Alf
 * @since 17.08.2022
 */
@Repository
public interface GlobalRoleRepository extends SuperIdEntityJpaRepository<GlobalRole> {
	Set<GlobalRole> findAllByUser_Id(long id);
}
