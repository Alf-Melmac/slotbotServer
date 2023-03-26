package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSquadIdDto;
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
	private final GuildService guildService;
	private final SlotService slotService;

	/**
	 * Updates the squadList of the given event to the given squadList
	 *
	 * @param squadList new squadList
	 * @param event     to update
	 */
	void updateSquadList(@NonNull List<MinimalSquadIdDto> squadList, @NonNull Event event) {
		List<Squad> eventSquads = event.getSquadList();
		if (eventSquads != null) {
			eventSquads.clear();
		} else {
			event.setSquadList(new ArrayList<>());
			eventSquads = event.getSquadList();
		}

		List<Squad> eventSquadList = new ArrayList<>();
		squadList.forEach(squadDto -> eventSquadList.add(updateOrCreateSquad(squadDto, event)));
		eventSquadList.removeAll(Collections.singletonList(null));
		eventSquads.addAll(eventSquadList);

		event.slotUpdateWithValidation();
	}

	/**
	 * Updates a squad with the given values identified by its id. If no squad can be found, a new one will be created.
	 * (!) Event can not be changed
	 *
	 * @param dto   with new values
	 * @param event is required when a new squad must be created
	 * @return updated Squad
	 */
	private Squad updateOrCreateSquad(@NonNull MinimalSquadIdDto dto, @NonNull Event event) {
		Squad squad = squadRepository.findById(dto.getId()).orElseGet(() -> Squad.builder().event(event).build());

		DtoUtils.ifPresent(dto.getName(), squad::setName);
		squad.setReservedFor(guildService.evaluateReservedFor(dto.getReservedFor()));

		if (dto.getSlotList() != null) {
			slotService.updateSlotList(dto.getSlotList(), squad);
		}

		return squad.deleteSquadIfEmpty() ? null : squad;
	}
}
