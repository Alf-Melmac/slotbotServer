package de.webalf.slotbot.feature.swap;

import de.webalf.slotbot.feature.swap.model.SwapRequest;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alf
 * @since 22.08.2023
 */
@Repository
interface SwapRequestRepository extends SuperIdEntityJpaRepository<SwapRequest> {
	Optional<SwapRequest> findByRequesterSlotAndForeignSlot(Slot requesterSlot, Slot foreignSlot);

	boolean existsByRequesterSlotAndForeignSlot(Slot requesterSlot, Slot foreignSlot);

	@Modifying
	@Query("UPDATE SwapRequest s SET s.messageId = :messageId WHERE s.id = :id")
	void updateMessageIdById(@Param("messageId") long messageId, @Param("id") long id);
}
