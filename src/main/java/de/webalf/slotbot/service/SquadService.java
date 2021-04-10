package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
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
import java.util.Collections;
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

	void updateSquadList(@NonNull List<SquadDto> squadList, @NonNull Event event) {
		List<Squad> eventSquads = event.getSquadList();
		if (eventSquads != null) {
			eventSquads.clear();
		} else {
			event.setSquadList(new ArrayList<>());
			eventSquads = event.getSquadList();
		}

		List<Squad> eventSquadList = new ArrayList<>();
		squadList.forEach(squadDto -> eventSquadList.add(updateSquad(squadDto, event)));
		eventSquadList.removeAll(Collections.singletonList(null));
		eventSquads.addAll(eventSquadList);

		event.slotUpdateWithValidation();
	}

	/**
	 * Updates a squad with the given values identified by its id
	 * (!) Event can not be changed
	 *
	 * @param dto   with new values
	 * @param event is required when a new squad must be created
	 * @return updated Squad
	 */
	private Squad updateSquad(@NonNull SquadDto dto, @NonNull Event event) {
		Squad squad = squadRepository.findById(dto.getId()).orElseGet(() -> Squad.builder().event(event).build());

		DtoUtils.ifPresent(dto.getName(), squad::setName);

		if (dto.getSlotList() != null) {
			slotService.updateSlotList(dto.getSlotList(), squad);
		}

		return squad.deleteSquadIfEmpty() ? null : squad;
	}
}
