package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.util.LongUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
public final class SlotAssembler {
	public static Slot fromDto(SlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		return Slot.builder()
				.id(slotDto.getId())
				.name(slotDto.getName())
				.number(slotDto.getNumber())
				.squad(SquadAssembler.fromDto(slotDto.getSquad()))
				.userId(LongUtils.parseLong(slotDto.getUserId()))
				.build();
	}

	public static SlotDto toDto(Slot slot) {
		return SlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				//Gibt sonst ne Endlosschleife
//				.squad(SquadAssembler.toDto(slot.getSquad()))
				.userId(Long.toString(slot.getUserId()))
				.build();
	}

	public static List<SlotDto> toDtoList(List<Slot> slotList) {
		return slotList.stream().map(SlotAssembler::toDto).collect(Collectors.toList());
	}

	public static Page<SlotDto> toDtoPage(Page<Slot> slotPage, Pageable pageable) {
		List<SlotDto> slotDtoList = toDtoList(slotPage.getContent());
		return new PageImpl<>(slotDtoList, pageable, slotPage.getTotalElements());
	}

	public static List<Slot> fromDtoList(List<SlotDto> slotDtoList) {
		return slotDtoList.stream().map(SlotAssembler::fromDto).collect(Collectors.toList());
	}
}
