package de.webalf.slotbot.assembler.website.event.edit;

import de.webalf.slotbot.assembler.website.event.creation.MinimalSlotAssembler;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSlotIdDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
final class MinimalSlotIdAssembler {
	private static MinimalSlotIdDto toDto(Slot slot) {
		MinimalSlotIdDto.MinimalSlotIdDtoBuilder<?, ?> builder = MinimalSlotIdDto.builder();
		MinimalSlotAssembler.toDto(builder, slot);
		return builder
				.id(slot.getId())
				.build();
	}

	static List<MinimalSlotIdDto> toDtoList(Iterable<? extends Slot> slots) {
		return StreamSupport.stream(slots.spliterator(), false)
				.map(MinimalSlotIdAssembler::toDto)
				.toList();
	}
}
