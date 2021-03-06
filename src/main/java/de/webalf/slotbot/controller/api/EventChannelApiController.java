package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.SquadDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.model.dtos.api.EventRecipientApiDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * Every action that can be performed in an event in a channel
 *
 * @author Alf
 * @since 17.08.2020
 */
@RequestMapping(API + "/events/channel/{channelId}")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventChannelApiController {
	private final EventService eventService;

	@GetMapping
	public EventApiDto getEventByChannelId(@PathVariable(value = "channelId") long channel) {
		log.trace("getEventByChannelId: " + channel);
		return EventApiAssembler.toDto(eventService.findByChannel(channel));
	}

	@DeleteMapping
	public void deleteEventByChannelId(@PathVariable(value = "channelId") long channel) {
		log.trace("deleteEventByChannelId: " + channel);
		eventService.deleteEvent(channel);
	}

	@PostMapping("/slot/{slotNumber}")
	public EventApiDto postSlot(@PathVariable(value = "channelId") long channel,
	                            @PathVariable(value = "slotNumber") int slotNumber,
	                            @RequestBody UserDto userDto) {
		log.trace("postSlot: " + channel + " " + slotNumber + " " + userDto.getId());
		return EventApiAssembler.toDto(eventService.slot(channel, slotNumber, userDto));
	}

	@PostMapping("/unslot")
	public EventApiDto postUnslot(@PathVariable(value = "channelId") long channel,
	                              @RequestBody UserDto userDto) {
		log.trace("postUnslot: " + channel + " " + userDto.getId());
		return EventApiAssembler.toDto(eventService.unslot(channel, userDto));
	}

	@PostMapping("/unslot/{slotNumber}")
	public EventRecipientApiDto postUnslotSlot(@PathVariable(value = "channelId") long channel,
	                                           @PathVariable(value = "slotNumber") int slotNumber) {
		log.trace("postUnslotSlot: " + channel + " " + slotNumber);
		return eventService.unslot(channel, slotNumber);
	}

	@PutMapping("/prepareSwap")
	public List<SlotDto> getSwapSlots(@PathVariable(value = "channelId") long channel,
	                                  @RequestBody List<UserDto> userDtos) {
		log.trace("getSwapSlots: " + channel);
		return SlotAssembler.toDtoList(eventService.findSwapSlots(channel, userDtos));
	}

	@PutMapping("/prepareSwap/{slotNumber}")
	public List<SlotDto> getSwapSlots(@PathVariable(value = "channelId") long channel,
	                                  @PathVariable(value = "slotNumber") int slotNumber,
	                                  @RequestBody UserDto userDto) {
		log.trace("getSwapSlots: " + channel + " SlotNumber: " + slotNumber + " user: " + userDto.getId());
		return SlotAssembler.toDtoList(eventService.findSwapSlots(channel, slotNumber, userDto));
	}

	@PutMapping("/blockSlot/{slotNumber}")
	public EventApiDto putBlockSlot(@PathVariable(value = "channelId") long channel,
	                                @PathVariable(value = "slotNumber") int slotNumber,
	                                @RequestBody(required = false) String replacementName) {
		log.trace("putBlockSlot: " + channel + " " + slotNumber + " " + replacementName);
		return EventApiAssembler.toDto(eventService.blockSlot(channel, slotNumber, replacementName));
	}

	@PutMapping("/renameSquad/{squadPosition}")
	public EventApiDto putRenameSquad(@PathVariable(value = "channelId") long channel,
	                                  @PathVariable(value = "squadPosition") int squadPosition,
	                                  @RequestBody SquadDto squadDto) {
		log.trace("putRenameSquad: " + channel + " " + squadPosition);
		return EventApiAssembler.toDto(eventService.renameSquad(channel, squadPosition, squadDto.getName()));
	}

	@PostMapping("/addSlot/{squadNumber}")
	public EventApiDto postAddSlot(@PathVariable(value = "channelId") long channel,
	                               @PathVariable(value = "squadNumber") int squadNumber,
	                               @RequestBody SlotDto slotDto) {
		log.trace("postAddSlot: " + channel + " " + squadNumber + " " + slotDto.getNumber());
		return EventApiAssembler.toDto(eventService.addSlot(channel, squadNumber, slotDto));
	}

	@DeleteMapping("/delSlot/{slotNumber}")
	public EventApiDto postDelSlot(@PathVariable(value = "channelId") long channel,
	                               @PathVariable(value = "slotNumber") int slotNumber) {
		log.trace("postDelSlot: " + channel + " " + slotNumber);
		return EventApiAssembler.toDto(eventService.deleteSlot(channel, slotNumber));
	}

	@PutMapping("/renameSlot/{slotNumber}")
	public EventApiDto putRenameSlot(@PathVariable(value = "channelId") long channel,
	                                 @PathVariable(value = "slotNumber") int slotNumber,
	                                 @RequestBody SlotDto slotDto) {
		log.trace("putRenameSlot: " + channel + " " + slotNumber);
		return EventApiAssembler.toDto(eventService.renameSlot(channel, slotNumber, slotDto.getName()));
	}
}
