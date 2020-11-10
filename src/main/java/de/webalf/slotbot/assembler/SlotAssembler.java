package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Component
public final class SlotAssembler {
	/**
	 * To be used if the focus relies on the slot
	 */
	public static Slot fromDto(SlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		return Slot.builder()
				.id(slotDto.getId())
				.name(slotDto.getName())
				.number(slotDto.getNumber())
				.squad(SquadAssembler.fromDto(slotDto.getSquad()))
				.user(UserAssembler.fromDto(slotDto.getUser()))
				.replacementText(slotDto.getReplacementText())
				.build();
	}

	/**
	 * To be used if the focus relies on the slot
	 */
	private static SlotDto toDto(Slot slot) {
		return SlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.squad(SquadAssembler.toDto(slot.getSquad()))
				.user(UserAssembler.toDto(slot.getUser()))
				.replacementText(slot.getReplacementText())
				.build();
	}

	/**
	 * To be used if the focus relies on the event
	 */
	private static SlotDto toEventDto(Slot slot) {
		//Don't add Squad here to prevent endless loops
		return SlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.user(UserAssembler.toDto(slot.getUser()))
				.replacementText(slot.getReplacementText())
				.build();
	}

	/**
	 * To be used if the focus relies on the slot
	 */
	public static List<SlotDto> toDtoList(List<Slot> slotList) {
		return slotList.stream().map(SlotAssembler::toDto).collect(Collectors.toList());
	}

	/**
	 * To be used if the focus relies on the event
	 */
	static List<SlotDto> toEventDtoList(Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(SlotAssembler::toEventDto)
				.sorted(Comparator.comparing(SlotDto::getNumber))
				.collect(Collectors.toList());
	}

	static List<Slot> fromDtoList(Iterable<? extends SlotDto> slotList) {
		if (slotList == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(slotList.spliterator(), false)
				.map(SlotAssembler::fromDto)
				.collect(Collectors.toList());
	}
}
