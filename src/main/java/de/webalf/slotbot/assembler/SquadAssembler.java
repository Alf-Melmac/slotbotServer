package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.SquadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
public final class SquadAssembler {
	public static Squad fromDto(SquadDto squadDto) {
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

	public static SquadDto toDto(Squad squad) {
		return SquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.slotList(SlotAssembler.toDtoList(squad.getSlotList()).stream().sorted(Comparator.comparing(SlotDto::getNumber)).collect(Collectors.toList()))
				//Gibt sonst ne Endlosschleife
//				.event(EventAssembler.toDto(squad.getEvent()))
				.build();
	}

	public static List<SquadDto> toDtoList(List<Squad> squadList) {
		return squadList.stream().map(SquadAssembler::toDto).collect(Collectors.toList());
	}

	public static Page<SquadDto> toDtoPage(Page<Squad> squadPage, Pageable pageable) {
		List<SquadDto> squadDtoList = toDtoList(squadPage.getContent());
		return new PageImpl<>(squadDtoList, pageable, squadPage.getTotalElements());
	}

	public static List<Squad> fromDtoList(List<SquadDto> squadList) {
		return squadList.stream().map(SquadAssembler::fromDto).collect(Collectors.toList());
	}
}
