package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.EventDiscordInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Repository
public interface EventDiscordInformationRepository extends JpaRepository<EventDiscordInformation, Long> {
	boolean existsByChannelIn(Collection<Long> channels);
}
