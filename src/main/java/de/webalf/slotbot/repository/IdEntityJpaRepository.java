package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.AbstractIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Alf
 * @since 22.12.2020
 */
public interface IdEntityJpaRepository<T extends AbstractIdEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
