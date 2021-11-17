package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.assembler.UserAssembler;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.api.SlotApiDto;
import de.webalf.slotbot.model.dtos.api.SlotApiViewDto;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 22.02.2021
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotApiAssembler {
	private final UserService userService;

	private static SlotApiDto toDto(Slot slot) {
		return SlotApiDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.user(UserAssembler.toDto(slot.getUser()))
				.replacementText(slot.getReplacementText())
				.build();
	}

	private SlotApiViewDto toViewDto(Slot slot) {
		return SlotApiViewDto.builder()
				.name(slot.getName())
				.number(slot.getNumber())
				.user(userService.toUserNameDto(slot.getUser(), slot.getEvent().getOwnerGuild()))
				.build();
	}

	public static List<SlotApiDto> toDtoList(Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(SlotApiAssembler::toDto)
				.collect(Collectors.toList());
	}

	public List<SlotApiViewDto> toViewDtoList(Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(this::toViewDto)
				.collect(Collectors.toList());
	}
}
