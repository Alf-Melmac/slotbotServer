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
import java.util.stream.StreamSupport;

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

	/**
	 * To be used if the focus relies on the event
	 */
	public static SquadDto toEventDto(Squad squad) {
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

	public static Page<SquadDto> toEventDtoPage(Page<Squad> squadPage, Pageable pageable) {
		List<SquadDto> squadDtoList = toEventDtoList(squadPage.getContent());
		return new PageImpl<>(squadDtoList, pageable, squadPage.getTotalElements());
	}

	static List<Squad> fromDtoList(Iterable<? extends SquadDto> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(SquadAssembler::fromDto)
				.collect(Collectors.toList());
	}
}
