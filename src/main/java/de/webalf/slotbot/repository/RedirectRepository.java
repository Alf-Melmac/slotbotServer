package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Redirect;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 03.12.2020
 */
@Repository
public interface RedirectRepository extends IdEntityJpaRepository<Redirect> {
	Optional<Redirect> findByEndpoint(String endpoint);
}
