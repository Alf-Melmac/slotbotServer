package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.FeatureFlag;
import de.webalf.slotbot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Alf
 * @since 11.07.2024
 */
@Repository
public interface FeatureFlagRepository extends SuperIdEntityJpaRepository<FeatureFlag> {
	@Query("SELECT DISTINCT f.feature from FeatureFlag f WHERE f.user = :user")
	Set<String> findDistinctFeaturesByUser(@Param("user") User user);

	Optional<FeatureFlag> findByFeatureAndUser(String feature, User user);

	Optional<FeatureFlag> findByFeatureAndUserNull(String feature);
}
