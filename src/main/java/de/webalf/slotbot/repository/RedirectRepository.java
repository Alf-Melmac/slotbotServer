package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Redirect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 03.12.2020
 */
@Repository
public interface RedirectRepository extends JpaRepository<Redirect, String> {
	Optional<Redirect> findByEndpoint(String endpoint);
}
