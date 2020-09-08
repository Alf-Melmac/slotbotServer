package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
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
				.build();
	}

	/**
	 * To be used if the focus relies on the event
	 */
	public static Slot fromEventDto(SlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		return Slot.builder()
				.id(slotDto.getId())
				.name(slotDto.getName())
				.number(slotDto.getNumber())
				.user(UserAssembler.fromDto(slotDto.getUser()))
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
	static List<SlotDto> toEventDtoList(List<Slot> slotList) {
		return slotList.stream().map(SlotAssembler::toEventDto).collect(Collectors.toList());
	}

	public static Page<SlotDto> toDtoPage(Page<Slot> slotPage, Pageable pageable) {
		List<SlotDto> slotDtoList = toEventDtoList(slotPage.getContent());
		return new PageImpl<>(slotDtoList, pageable, slotPage.getTotalElements());
	}

	static List<Slot> fromDtoList(List<SlotDto> slotDtoList) {
		return slotDtoList.stream().map(SlotAssembler::fromDto).collect(Collectors.toList());
	}
}
