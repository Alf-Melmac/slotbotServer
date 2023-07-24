package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.api.SquadApiViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 08.06.2023
 */
@Component
@RequiredArgsConstructor
class SquadApiViewAssembler {
	private final SlotApiViewAssembler slotApiAssembler;

	private SquadApiViewDto toViewDto(Squad squad) {
		return SquadApiViewDto.builder()
				.name(squad.getName())
				.slotList(slotApiAssembler.toViewDtoList(squad.getSlotList()))
				.build();
	}

	List<SquadApiViewDto> toViewDtoList(Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(this::toViewDto)
				.toList();
	}
}
