package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.api.SquadApiViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 22.02.2021
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SquadApiAssembler {
	private final SlotApiAssembler slotApiAssembler;

	private SquadApiViewDto toViewDto(Squad squad) {
		return SquadApiViewDto.builder()
				.name(squad.getName())
				.slotList(slotApiAssembler.toViewDtoList(squad.getSlotList()))
				.build();
	}

	public List<SquadApiViewDto> toViewDtoList(Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(this::toViewDto)
				.collect(Collectors.toList());
	}
}
