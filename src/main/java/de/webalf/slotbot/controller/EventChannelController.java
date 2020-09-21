package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.EventRecipientDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Every action that can be performed in an event in a channel
 *
 * @author Alf
 * @since 17.08.2020
 */
@RequestMapping("/events/channel/{channelId}")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventChannelController {
	private final EventService eventService;

	@GetMapping("")
	public EventDto getEventByChannelId(@PathVariable(value = "channelId") long channel) {
		log.trace("getEventByChannelId: " + channel);
		return EventAssembler.toDto(eventService.findByChannel(channel));
	}

	@DeleteMapping("")
	public void deleteEventByChannelId(@PathVariable(value = "channelId") long channel) {
		log.trace("deleteEventByChannelId: " + channel);
		eventService.deleteEvent(channel);
	}

	@PostMapping("/slot/{slotNumber}")
	public EventDto postSlot(@PathVariable(value = "channelId") long channel,
	                         @PathVariable(value = "slotNumber") int slotNumber,
	                         @RequestBody UserDto userDto) {
		log.trace("postSlot: " + channel + " " + slotNumber + " " + userDto.getId());
		return EventAssembler.toDto(eventService.slot(channel, slotNumber, userDto));
	}

	@PostMapping("/unslot")
	public EventDto postUnslot(@PathVariable(value = "channelId") long channel,
	                           @RequestBody UserDto userDto) {
		log.trace("postUnslot: " + channel + " " + userDto.getId());
		return EventAssembler.toDto(eventService.unslot(channel, userDto));
	}

	@PostMapping("/unslot/{slotNumber}")
	public EventRecipientDto postUnslotSlot(@PathVariable(value = "channelId") long channel,
	                                        @PathVariable(value = "slotNumber") int slotNumber) {
		log.trace("postUnslotSlot: " + channel + " " + slotNumber);
		return eventService.unslot(channel, slotNumber);
	}

	@PostMapping("/addSlot/{squadNumber}")
	public EventDto postAddSlot(@PathVariable(value = "channelId") long channel,
	                            @PathVariable(value = "squadNumber") int squadNumber,
	                            @RequestBody SlotDto slotDto) {
		log.trace("postAddSlot: " + channel + " " + squadNumber + " " + slotDto.getNumber());
		return EventAssembler.toDto(eventService.addSlot(channel, squadNumber, slotDto));
	}

	@DeleteMapping("/delSlot/{slotNumber}")
	public EventDto postDelSlot(@PathVariable(value = "channelId") long channel,
	                            @PathVariable(value = "slotNumber") int slotNumber) {
		log.trace("postDelSlot: " + channel + " " + slotNumber);
		return EventAssembler.toDto(eventService.deleteSlot(channel, slotNumber));
	}

	@PutMapping("/renameSlot/{slotNumber}")
	public EventDto putRenameSlot(@PathVariable(value = "channelId") long channel,
	                              @PathVariable(value = "slotNumber") int slotNumber,
	                              @RequestBody SlotDto slotDto) {
		log.trace("putRenameSlot: " + channel + " " + slotNumber);
		return EventAssembler.toDto(eventService.renameSlot(channel, slotNumber, slotDto.getName()));
	}

	@PutMapping("/prepareSwap/{slotNumber}")
	public List<SlotDto> getSwapSlots(@PathVariable(value = "channelId") long channel,
	                                  @PathVariable(value = "slotNumber") int slotNumber,
	                                  @RequestBody UserDto userDto) {
		log.trace("getSwapSlots: " + channel + " SlotNumber: " + slotNumber + " user: " + userDto.getId());
		return SlotAssembler.toDtoList(eventService.findSwapSlots(channel, slotNumber, userDto));
	}
}
