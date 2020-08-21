package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {
	private final EventRepository eventRepository;
	private final SlotService slotService;

	public Event createEvent(EventDto eventDto) {
		Event event = EventAssembler.fromDto(eventDto);
		if (event.hasDuplicatedSlotNumber()) {
			throw BusinessRuntimeException.builder().title("Slotnummern müssen innerhalb eines Events eindeutig sein.").build();
		}

		//Ich habe doch auch keine Ahnung was ich tue
		for (Squad squad : event.getSquadList()) {
			squad.setEvent(event);
			for (Slot slot : squad.getSlotList()) {
				slot.setSquad(squad);
			}
		}

		return eventRepository.save(event);
	}

	public Event findByChannel(long channel) {
		return eventRepository.findByChannel(channel).orElseThrow(ResourceNotFoundException::new);
	}

	public Event updateEvent(long eventId, EventDto dto) {
		Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		//TODO Validation

		DtoUtils.ifPresent(dto.getName(), event::setName);
		DtoUtils.ifPresent(dto.getDate(), event::setDate);
		DtoUtils.ifPresent(dto.getStartTime(), event::setTime);
		DtoUtils.ifPresent(dto.getDescription(), event::setDescription);
		DtoUtils.ifPresent(dto.getChannel(), event::setChannelString);
		DtoUtils.ifPresent(dto.getInfoMsg(), event::setInfoMsgString);
		DtoUtils.ifPresent(dto.getSlotListMsg(), event::setSlotListMsgString);

		//TODO Squad
//		DtoUtils.ifPresent(dto.getSquadList(), event::setSquadList);

		return eventRepository.save(event);
	}

	/**
	 * Searches for the given channel the matching event and enters the given userId for the slot with given number, if available.
	 *
	 * @param channel    event channel
	 * @param slotNumber Slot to slot into
	 * @param userId     person that should be slotted
	 * @return Event in which the person has been slotted
	 * @throws BusinessRuntimeException if the slot is already occupied
	 */
	public Event slot(long channel, int slotNumber, long userId) throws BusinessRuntimeException {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		event.findSlotOfUser(userId).ifPresent(alreadySlottedSlot -> unslot(event, alreadySlottedSlot, userId));
		slotService.slot(slot, userId);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and removes the user, found by userId, from its slot.
	 *
	 * @param channel event channel
	 * @param userId  person that should be unslotted
	 * @return Event in which the person has been unslotted
	 */
	public Event unslot(long channel, long userId) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlotOfUser(userId).orElseThrow(ResourceNotFoundException::new);
		return unslot(event, slot, userId);
	}

	/**
	 * Removes the user from the given slot in the given event.
	 *
	 * @param event  event in which the unslot should be performed
	 * @param slot   slot which should be freed from the user
	 * @param userId user to unslot
	 * @return Event in which the unslotted has been performed
	 */
	private Event unslot(Event event, Slot slot, long userId) {
		slotService.unslot(slot, userId);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and adds the given slot to the squad found by squadNumber.
	 *
	 * @param channel     event channel
	 * @param squadNumber Counted, starting by 0
	 * @param slotDto     slot to add
	 * @return event in which the slot has been added
	 */
	public Event addSlot(long channel, int squadNumber, SlotDto slotDto) {
		Event event = findByChannel(channel);
		event.getSquadList().get(squadNumber).addSlot(slotService.newSlot(slotDto));
		//TODO Maybe use updateEvent(...)
		return eventRepository.save(event);
	}

	/**
	 * Searches for the given channel the matching event and removes the slot by number.
	 *
	 * @param channel    event channel
	 * @param slotNumber to delete
	 * @return event in which the slot has been deleted
	 */
	public Event deleteSlot(long channel, int slotNumber) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		if (slot.getUserId() != 0) {
			throw new ForbiddenException("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.");
		}
		slot.getSquad().deleteSlot(slot);
		slotService.deleteSlot(slot);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and returns the slots matching the given slotNumber and userId
	 *
	 * @param channel    event channel
	 * @param slotNumber slot1 to find by slotNumber
	 * @param userId     slot2 to find by user
	 * @return two Slots
	 */
	public List<Slot> findSwapSlots(long channel, int slotNumber, long userId) {
		Event event = findByChannel(channel);
		return Arrays.asList(
				event.findSlotOfUser(userId).orElseThrow(ResourceNotFoundException::new),
				event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new)
		);
	}
}
