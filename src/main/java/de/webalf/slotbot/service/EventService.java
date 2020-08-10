package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			//TODO BusinessRuntimeException
			throw new RuntimeException("Da ist nen doppelte Slotnummer. Die müssen aber eindeutig sein");
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
	 * Sucht für den übergebenen Kanal das passende Event zu der Slotnummer und trägt, falls vorhanden, die übergebene UserId für den Slot ein.
	 *
	 * @param channel    Event-Channel
	 * @param slotNumber Slot to slot into
	 * @param userId     person that should be slotted
	 * @return Event in which the person has been slotted
	 */
	public Event slot(long channel, int slotNumber, long userId) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		try {
			event.findSlotOfUser(userId).ifPresent(alreadySlottedSlot -> unslot(event, alreadySlottedSlot, userId));
		} catch (RuntimeException e) {
			//TODO BRE
			throw new RuntimeException("Der Slot ist belegt");
		}
		slotService.slot(slot, userId);
		return event;
	}

	public Event unslot(long channel, long userId) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlotOfUser(userId).orElseThrow(ResourceNotFoundException::new);
		return unslot(event, slot, userId);
	}

	private Event unslot(Event event, Slot slot, long userId) {
		slotService.unslot(slot, userId);
		return event;
	}
}
