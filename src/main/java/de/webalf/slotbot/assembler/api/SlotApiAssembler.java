package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.model.Slot;
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

	private SlotApiViewDto toViewDto(Slot slot) {
		return SlotApiViewDto.builder()
				.name(slot.getName())
				.number(slot.getNumber())
				.user(userService.toUserNameDto(slot.getUser()))
				.build();
	}

	public List<SlotApiViewDto> toViewDtoList(Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(this::toViewDto)
				.collect(Collectors.toList());
	}
}
