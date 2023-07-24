package de.webalf.slotbot.assembler.api.event;

import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.api.event.AbstractSquadApiDto;
import de.webalf.slotbot.model.dtos.api.event.creation.SquadApiDto;
import de.webalf.slotbot.model.dtos.api.event.view.SquadApiIdDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 22.02.2021
 */
@Component
@RequiredArgsConstructor
final class SquadApiAssembler {
	private final SlotApiAssembler slotAssembler;
	private final GuildService guildService;

	private Squad fromDto(@NonNull SquadApiDto dto) {
		return Squad.builder()
				.name(dto.getName().trim())
				.slotList(slotAssembler.fromDtoList(dto.getSlotList()))
				.reservedFor(guildService.evaluateReservedFor(dto.getReservedForGuildId()))
				.build();
	}

	List<Squad> fromDtoList(Iterable<? extends SquadApiDto> dtos) {
		if (dtos == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(dtos.spliterator(), false)
				.map(this::fromDto)
				.toList();
	}

	private static <C extends AbstractSquadApiDto, B extends AbstractSquadApiDto.AbstractSquadApiDtoBuilder<C, B>> void toDto(AbstractSquadApiDto.AbstractSquadApiDtoBuilder<C, B> builder, @NonNull Squad squad) {
		builder
				.name(squad.getName())
				.reservedForGuildId(DtoUtils.getIdStringIfPresent(squad.getReservedFor()));
	}

	private static SquadApiIdDto toIdDto(@NonNull Squad squad) {
		SquadApiIdDto.SquadApiIdDtoBuilder<?, ?> builder = SquadApiIdDto.builder();
		toDto(builder, squad);
		return builder
				.id(squad.getId())
				.slotList(SlotApiAssembler.toIdDtoList(squad.getSlotList()))
				.build();
	}

	static List<SquadApiIdDto> toIdDtoList(Iterable<? extends Squad> squads) {
		return StreamSupport.stream(squads.spliterator(), false)
				.map(SquadApiAssembler::toIdDto)
				.toList();
	}
}
