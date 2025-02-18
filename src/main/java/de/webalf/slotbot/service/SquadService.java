package de.webalf.slotbot.service;

import de.webalf.slotbot.feature.requirement.RequirementService;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSquadIdDto;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SquadService {
	private final GuildService guildService;
	private final RequirementService requirementService;
	private final SlotService slotService;

	/**
	 * Updates the squadList of the given event to the given squadList
	 *
	 * @param squadList new squadList
	 * @param event     to update
	 */
	void updateSquadList(@NonNull List<MinimalSquadIdDto> squadList, @NonNull Event event) {
		final List<Long> retainedSquadIds = squadList.stream().map(MinimalSquadIdDto::getId).filter(id -> id != 0).toList();
		if (event.getSquadList() == null) {
			event.setSquadList(new ArrayList<>());
		}
		final List<Squad> eventSquads = event.getSquadList();
		//Remove squads that are not in the new list
		eventSquads.removeIf(squad -> !retainedSquadIds.contains(squad.getId()));
		//Ensure that eventSquads have the same order as the squadList
		eventSquads.sort((s1, s2) -> {
			final int index1 = retainedSquadIds.indexOf(s1.getId());
			final int index2 = retainedSquadIds.indexOf(s2.getId());
			return Integer.compare(index1, index2);
		});

		//Count the added/updated squads to add new squads at the correct index
		final AtomicInteger squadsUpdated = new AtomicInteger();
		//For each new squad, find the matching existing squad by id, if not found create a new one
		squadList.forEach(squadDto -> {
			final int squadIndex = squadsUpdated.getAndIncrement();
			final Squad squad = eventSquads.stream()
					.filter(s -> s.getId() == squadDto.getId() && squadDto.getId() != 0)
					.findAny()
					.orElseGet(() -> {
						final Squad newSquad = Squad.builder().event(event).build();
						eventSquads.add(squadIndex, newSquad);
						return newSquad;
					});
			updateSquad(squadDto, squad);
		});

		event.removeEmptySquads();
		event.slotUpdateWithValidation();
	}

	/**
	 * Updates the given squad with the values from the given dto
	 * (!) Event can not be changed
	 *
	 * @param dto   new values
	 * @param squad to update
	 */
	private void updateSquad(@NonNull MinimalSquadIdDto dto, @NonNull Squad squad) {
		DtoUtils.ifPresent(dto.getName(), squad::setName);
		squad.setReservedFor(guildService.evaluateReservedFor(dto.getReservedFor()));
		DtoUtils.ifPresentObject(dto.getRequirements(), requirements -> squad.setRequirements(requirementService.find(requirements)));
		if (dto.getSlotList() != null) {
			slotService.updateSlotList(dto.getSlotList(), squad);
		}
	}
}
