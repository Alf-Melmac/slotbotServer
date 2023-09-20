package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SquadDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 23.06.2020
 */
@UtilityClass
final class SquadAssembler {
	static Squad fromDto(SquadDto squadDto) {
		if (squadDto == null) {
			return null;
		}

		return Squad.builder()
				.id(squadDto.getId())
				.name(squadDto.getName().trim())
				.slotList(SlotAssembler.fromDtoList(squadDto.getSlotList()))
				.reservedFor(GuildAssembler.fromDto(squadDto.getReservedFor()))
				.build();
	}
}
