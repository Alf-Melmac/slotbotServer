package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.SlotDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 23.06.2020
 */
@UtilityClass
public final class SlotAssembler {
	public static Slot fromDto(SlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		return Slot.builder()
				.id(slotDto.getId())
				.name(slotDto.getName().trim())
				.number(slotDto.getNumber())
				.squad(SquadAssembler.fromDto(slotDto.getSquad()))
				.reservedFor(GuildAssembler.fromDto(slotDto.getReservedFor()))
				.user(UserAssembler.fromDto(slotDto.getUser()))
				.replacementText(slotDto.getReplacementText())
				.build();
	}

	static List<Slot> fromDtoList(Iterable<? extends SlotDto> slotList) {
		if (slotList == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(slotList.spliterator(), false)
				.map(SlotAssembler::fromDto)
				.toList();
	}
}
