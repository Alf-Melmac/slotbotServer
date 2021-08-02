package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Alf
 * @since 02.08.2021
 */
public interface SuperIdEntityJpaRepository<T extends AbstractSuperIdEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
