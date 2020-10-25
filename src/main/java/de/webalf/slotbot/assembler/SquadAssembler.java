package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.SquadDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
public final class SquadAssembler {
	static Squad fromDto(SquadDto squadDto) {
		if (squadDto == null) {
			return null;
		}

		return Squad.builder()
				.id(squadDto.getId())
				.name(squadDto.getName())
				.slotList(SlotAssembler.fromDtoList(squadDto.getSlotList()))
				.event(EventAssembler.fromDto(squadDto.getEvent()))
				.build();
	}

	/**
	 * To be used if the focus relies on the event
	 */
	private static SquadDto toEventDto(Squad squad) {
		//Don't add Event here to prevent endless loops
		return SquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.slotList(SlotAssembler.toEventDtoList(squad.getSlotList()).stream().sorted(Comparator.comparing(SlotDto::getNumber)).collect(Collectors.toList()))
				.build();
	}

	/**
	 * To be used if the focus relies on a slot
	 */
	static SquadDto toDto(Squad squad) {
		return SquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.event(EventAssembler.toSlotDto(squad.getEvent()))
				.build();
	}

	static List<SquadDto> toEventDtoList(Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(SquadAssembler::toEventDto)
				.collect(Collectors.toList());
	}

	static List<Squad> fromDtoList(Iterable<? extends SquadDto> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(SquadAssembler::fromDto)
				.collect(Collectors.toList());
	}
}
