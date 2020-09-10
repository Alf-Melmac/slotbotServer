package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.repository.SquadRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alf
 * @since 10.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SquadService {
	private final SquadRepository squadRepository;

	/**
	 * Deletes the given squad, if no person is slotted on any slot
	 *
	 * @param squad to be deleted
	 */
	void deleteSquadIfEmpty(@NonNull Squad squad) {
		if (squad.getSlotList().stream().noneMatch(Slot::isNotEmpty)) {
			squad.getEvent().removeSquad(squad);
			squadRepository.delete(squad);
		}
	}
}
