package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SquadDto;
import de.webalf.slotbot.model.dtos.referenceless.SquadReferencelessDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 23.06.2020
 */
@UtilityClass
public final class SquadAssembler {
	static Squad fromDto(SquadDto squadDto) {
		if (squadDto == null) {
			return null;
		}

		return Squad.builder()
				.id(squadDto.getId())
				.name(squadDto.getName().trim())
				.slotList(SlotAssembler.fromDtoList(squadDto.getSlotList()))
				.event(EventAssembler.fromDto(squadDto.getEvent()))
				.build();
	}

	static List<Squad> fromDtoList(Iterable<? extends SquadDto> squadList) {
		if (squadList == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(squadList.spliterator(), false)
				.map(SquadAssembler::fromDto)
				.collect(Collectors.toList());
	}

	/**
	 * To be used if the focus relies on the event
	 */
	private static SquadReferencelessDto toReferencelessDto(Squad squad) {
		return SquadReferencelessDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.slotList(SlotAssembler.toReferencelessDtoList(squad.getSlotList()))
				.build();
	}

	static List<SquadReferencelessDto> toReferencelessDtoList(Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(SquadAssembler::toReferencelessDto)
				.collect(Collectors.toList());
	}

	/**
	 * To be used if the focus relies on a slot
	 */
	static SquadDto toDto(Squad squad) {
		return SquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.event(EventAssembler.toAbstractDto(squad.getEvent()))
				.build();
	}
}
