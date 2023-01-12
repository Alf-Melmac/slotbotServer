package de.webalf.slotbot.assembler.website.event.creation;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSlotDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.util.GuildUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class MinimalSlotAssembler {
	private final UserService userService;
	private final GuildService guildService;

	private Slot fromDto(MinimalSlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		return Slot.builder()
				.name(slotDto.getName().trim())
				.number(slotDto.getNumber())
				.reservedFor(evaluateReservedFor(slotDto.getReservedFor()))
				.user(slotDto.isBlocked() ? userService.getDefaultUser() : null)
				.replacementText(slotDto.isBlocked() ? slotDto.getReplacementText() : null)
				.build();
	}

	List<Slot> fromDtoList(Iterable<? extends MinimalSlotDto> slotList) {
		if (slotList == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(slotList.spliterator(), false)
				.map(this::fromDto)
				.toList();
	}

	Guild evaluateReservedFor(String reservedFor) {
		return StringUtils.isNotEmpty(reservedFor) ? guildService.find(Long.parseLong(reservedFor)) : null;
	}

	public static <C extends MinimalSlotDto, B extends MinimalSlotDto.MinimalSlotDtoBuilder<C, B>> MinimalSlotDto.MinimalSlotDtoBuilder<C, B>
	toDto(MinimalSlotDto.MinimalSlotDtoBuilder<C, B> builder, @NonNull Slot slot) {
		return builder
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedFor(GuildUtils.getReservedFor(slot))
				.blocked(slot.isBlocked())
				.replacementText(slot.getReplacementText());
	}

	private static MinimalSlotDto toDto(@NonNull Slot slot) {
		return toDto(MinimalSlotDto.builder(), slot).build();
	}

	static List<MinimalSlotDto> toDtoList(Iterable<? extends Slot> slots) {
		return StreamSupport.stream(slots.spliterator(), false)
				.map(MinimalSlotAssembler::toDto)
				.toList();
	}
}
