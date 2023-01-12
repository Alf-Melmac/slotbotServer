package de.webalf.slotbot.assembler.website.event.edit;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSquadIdDto;
import de.webalf.slotbot.util.GuildUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
final class MinimalSquadIdAssembler {
	private static MinimalSquadIdDto toDto(@NonNull Squad squad) {
		return MinimalSquadIdDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.slotList(MinimalSlotIdAssembler.toDtoList(squad.getSlotList()))
				.reservedFor(GuildUtils.getReservedFor(squad))
				.build();
	}

	static List<MinimalSquadIdDto> toDtoList(Iterable<? extends Squad> squads) {
		return StreamSupport.stream(squads.spliterator(), false)
				.map(MinimalSquadIdAssembler::toDto)
				.toList();
	}
}
