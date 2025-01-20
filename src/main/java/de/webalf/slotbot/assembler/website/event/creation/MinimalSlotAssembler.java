package de.webalf.slotbot.assembler.website.event.creation;

import de.webalf.slotbot.feature.requirement.RequirementService;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSlotDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.util.GuildUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Component
@RequiredArgsConstructor
public final class MinimalSlotAssembler {
	private final UserService userService;
	private final GuildService guildService;
	private final RequirementService requirementService;

	private Slot fromDto(MinimalSlotDto slotDto) {
		if (slotDto == null) {
			return null;
		}

		final boolean blocked = slotDto.isBlocked();
		final String replacementText = slotDto.getReplacementText();
		return Slot.builder()
				.name(slotDto.getName().trim())
				.number(slotDto.getNumber())
				.reservedFor(guildService.evaluateReservedFor(slotDto.getReservedFor()))
				.requirements(requirementService.find(slotDto.getRequirements()))
				.user(blocked ? userService.getDefaultUser() : null)
				.replacementText(blocked && StringUtils.isNotEmpty(replacementText) ? replacementText.trim() : null)
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

	public static <C extends MinimalSlotDto, B extends MinimalSlotDto.MinimalSlotDtoBuilder<C, B>> B
	toDto(MinimalSlotDto.MinimalSlotDtoBuilder<C, B> builder, @NonNull Slot slot) {
		return builder
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedFor(GuildUtils.getReservedFor(slot))
				.requirements(slot.getRequirementsIds())
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
