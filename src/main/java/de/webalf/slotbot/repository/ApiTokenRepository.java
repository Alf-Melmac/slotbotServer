package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.authentication.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 06.03.2021
 */
@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, String> {
}
