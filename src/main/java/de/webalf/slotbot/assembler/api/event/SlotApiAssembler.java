package de.webalf.slotbot.assembler.api.event;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.event.creation.SlotApiDto;
import de.webalf.slotbot.model.dtos.api.event.view.SlotApiIdDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
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
final class SlotApiAssembler {
	private final UserService userService;
	private final GuildService guildService;

	private Slot fromDto(@NonNull SlotApiDto dto) {
		final String userId = dto.getUserId();
		final User user = StringUtils.isNotEmpty(userId) ? userService.find(Long.parseLong(userId)) : null;
		return Slot.builder()
				.name(dto.getName().trim())
				.number(dto.getNumber())
				.reservedFor(guildService.evaluateReservedFor(dto.getReservedForGuildId()))
				.user(user)
				.replacementText(user != null && user.isDefaultUser() ? dto.getReplacementText() : null)
				.build();
	}

	List<Slot> fromDtoList(Iterable<? extends SlotApiDto> dtos) {
		if (dtos == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(dtos.spliterator(), false)
				.map(this::fromDto)
				.toList();
	}

	private static <C extends SlotApiDto, B extends SlotApiDto.SlotApiDtoBuilder<C, B>> void toDto(SlotApiDto.SlotApiDtoBuilder<C, B> builder, @NonNull Slot slot) {
		builder
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedForGuildId(DtoUtils.getIdStringIfPresent(slot.getReservedFor()))
				.userId(DtoUtils.getIdStringIfPresent(slot.getUser()))
				.replacementText(slot.getReplacementText());
	}

	private static SlotApiIdDto toIdDto(@NonNull Slot slot) {
		SlotApiIdDto.SlotApiIdDtoBuilder<?, ?> builder = SlotApiIdDto.builder();
		toDto(builder, slot);
		return builder
				.id(slot.getId())
				.build();
	}

	static List<SlotApiIdDto> toIdDtoList(Iterable<? extends Slot> slots) {
		return StreamSupport.stream(slots.spliterator(), false)
				.map(SlotApiAssembler::toIdDto)
				.toList();
	}
}
