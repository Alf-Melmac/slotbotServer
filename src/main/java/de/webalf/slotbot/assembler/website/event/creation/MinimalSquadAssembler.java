package de.webalf.slotbot.assembler.website.event.creation;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSquadDto;
import de.webalf.slotbot.util.GuildUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
final class MinimalSquadAssembler {
	private final MinimalSlotAssembler slotAssembler;

	private Squad fromDto(MinimalSquadDto squadDto) {
		if (squadDto == null) {
			return null;
		}

		return Squad.builder()
				.name(squadDto.getName().trim())
				.slotList(slotAssembler.fromDtoList(squadDto.getSlotList()))
				.reservedFor(slotAssembler.evaluateReservedFor(squadDto.getReservedFor()))
				.build();
	}

	List<Squad> fromDtoList(Iterable<? extends MinimalSquadDto> squadList) {
		if (squadList == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(squadList.spliterator(), false)
				.map(this::fromDto)
				.collect(Collectors.toList());
	}

	private static MinimalSquadDto toDto(Squad squad) {
		return MinimalSquadDto.builder()
				.name(squad.getName())
				.slotList(MinimalSlotAssembler.toDtoList(squad.getSlotList()))
				.reservedFor(GuildUtils.getReservedFor(squad))
				.build();
	}

	public static List<MinimalSquadDto> toDtoList(Iterable<? extends Squad> squads) {
		return StreamSupport.stream(squads.spliterator(), false)
				.map(MinimalSquadAssembler::toDto)
				.collect(Collectors.toList());
	}
}
