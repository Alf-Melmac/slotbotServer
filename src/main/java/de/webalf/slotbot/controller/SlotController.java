package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author Alf
 * @since 21.08.2020
 */
@RequestMapping("/slots")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotController {
	private final SlotService slotService;

	@PutMapping("/swap")
	public EventDto putSwap(@RequestBody ArrayList<SlotDto> slots) {
		return EventAssembler.toDto(slotService.swap(slots));
	}
}
