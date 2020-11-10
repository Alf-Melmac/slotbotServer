package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SquadDto;
import de.webalf.slotbot.repository.SquadRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SquadService {
	private final SquadRepository squadRepository;
	private final SlotService slotService;

	List<Squad> updateSquadList(@NonNull List<SquadDto> squadList) {
		List<Squad> eventSquadList = new ArrayList<>();
		squadList.forEach(squadDto -> eventSquadList.add(updateSquad(squadDto)));
		return eventSquadList;
	}

	/**
	 * Updates a squad with the given values identified by its id
	 * (!) Event can not be changes
	 *
	 * @param dto with new values
	 * @return updated Squad
	 */
	private Squad updateSquad(SquadDto dto) {
		Squad squad = squadRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		DtoUtils.ifPresent(dto.getName(), squad::setName);

		if (dto.getSlotList() != null) {
			squad.getSlotList().clear();
			squad.getSlotList().addAll(slotService.updateSlotList(dto.getSlotList()));
		}

		return squad;
	}
}
