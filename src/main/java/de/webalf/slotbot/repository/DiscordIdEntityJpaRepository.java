package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.AbstractDiscordIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Alf
 * @since 04.01.2022
 */
public interface DiscordIdEntityJpaRepository<T extends AbstractDiscordIdEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
