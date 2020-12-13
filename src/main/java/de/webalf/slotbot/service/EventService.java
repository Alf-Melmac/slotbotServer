package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.api.EventRecipientApiDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {
	private final EventRepository eventRepository;
	private final SquadService squadService;
	private final SlotService slotService;
	private final UserService userService;

	public Event createEvent(@NonNull EventDto eventDto) {
		if (!StringUtils.isEmpty(eventDto.getChannel()) && eventRepository.findByChannel(LongUtils.parseLong(eventDto.getChannel())).isPresent()) {
			throw BusinessRuntimeException.builder().title("In diesem Kanal gibt es bereits ein Event.").build();
		}
		Event event = EventAssembler.fromDto(eventDto);

		//Ich habe doch auch keine Ahnung was ich tue
		for (Squad squad : event.getSquadList()) {
			squad.setEvent(event);
			for (Slot slot : squad.getSlotList()) {
				slot.setSquad(squad);
			}
		}

		return eventRepository.save(event);
	}

	/**
	 * Returns the event associated with the given channelId
	 *
	 * @param channel to find event for
	 * @return Event from channel
	 * @throws ResourceNotFoundException if no event with this channelId could be found
	 */
	public Event findByChannel(long channel) {
		return eventRepository.findByChannel(channel).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Returns the event associated with the given eventId
	 *
	 * @param eventId to find event for
	 * @return Event found by id
	 * @throws ResourceNotFoundException if no event with this eventId could be found
	 */
	public Event findById(long eventId) {
		return eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Returns all events that take place in the specified period
	 *
	 * @return all events in given period
	 */
	public List<Event> findAllBetween(LocalDateTime start, LocalDateTime end) {
		if (PermissionService.hasEventManageRole()) {
			return eventRepository.findAllByDateTimeBetween(start, end);
		}
		return eventRepository.findAllByDateTimeBetweenAndHiddenFalse(start, end);
	}

	public Event updateEvent(@NonNull EventDto dto) {
		Event event = eventRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		if (!StringUtils.isEmpty(dto.getChannel())
				&& eventRepository.findByChannel(LongUtils.parseLong(dto.getChannel())).filter(event1 -> !event1.equals(event)).isPresent()) {
			throw BusinessRuntimeException.builder().title("In diesem Kanal gibt es bereits ein Event.").build();
		}

		DtoUtils.ifPresent(dto.getName(), event::setName);
		DtoUtils.ifPresent(dto.getDate(), event::setDate);
		DtoUtils.ifPresent(dto.getStartTime(), event::setTime);
		DtoUtils.ifPresent(dto.getCreator(), event::setCreator);
		DtoUtils.ifPresent(dto.getHidden(), event::setHidden);
		DtoUtils.ifPresentOrEmpty(dto.getChannel(), event::setChannelString);
		DtoUtils.ifPresentOrEmpty(dto.getInfoMsg(), event::setInfoMsgString);
		DtoUtils.ifPresentOrEmpty(dto.getSlotListMsg(), event::setSlotListMsgString);
		DtoUtils.ifPresentOrEmpty(dto.getDescription(), event::setDescription);
		DtoUtils.ifPresentOrEmpty(dto.getPictureUrl(), event::setPictureUrl);
		DtoUtils.ifPresentOrEmpty(dto.getMissionType(), event::setMissionType);
		DtoUtils.ifPresent(dto.getRespawn(), event::setRespawn);
		DtoUtils.ifPresentOrEmpty(dto.getMissionLength(), event::setMissionLength);
		DtoUtils.ifPresent(dto.getReserveParticipating(), event::setReserveParticipating);
		DtoUtils.ifPresentOrEmpty(dto.getModPack(), event::setModPack);
		DtoUtils.ifPresentOrEmpty(dto.getMap(), event::setMap);
		DtoUtils.ifPresentOrEmpty(dto.getMissionTime(), event::setMissionTime);
		DtoUtils.ifPresentOrEmpty(dto.getNavigation(), event::setNavigation);
		DtoUtils.ifPresentOrEmpty(dto.getTechnicalTeleport(), event::setTechnicalTeleport);
		DtoUtils.ifPresentOrEmpty(dto.getMedicalSystem(), event::setMedicalSystem);

		if (dto.getSquadList() != null) {
			squadService.updateSquadList(dto.getSquadList(), event);
		}

		return event;
	}

	/**
	 * Searches for the given channel the matching event and deletes it
	 *
	 * @param channel event channel
	 */
	public void deleteEvent(long channel) {
		eventRepository.delete(findByChannel(channel));
	}

	/**
	 * Searches for the given channel the matching event and enters the given user for the slot with given number, if available.
	 *
	 * @param channel    event channel
	 * @param slotNumber Slot to slot into
	 * @param userDto    person that should be slotted
	 * @return Event in which the person has been slotted
	 * @throws BusinessRuntimeException if the slot is already occupied
	 */
	public Event slot(long channel, int slotNumber, UserDto userDto) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		User user = userService.find(userDto);
		slotService.slot(slot, user);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and removes the user, found by user, from its slot.
	 *
	 * @param channel event channel
	 * @param userDto person that should be unslotted
	 * @return Event in which the person has been unslotted
	 */
	public Event unslot(long channel, UserDto userDto) {
		Event event = findByChannel(channel);
		User user = userService.find(userDto);
		Slot slot = event.findSlotOfUser(user).orElseThrow(ResourceNotFoundException::new);
		slotService.unslot(slot, user);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and removes the user, found by slotNumber, from its slot.
	 *
	 * @param channel    event channel
	 * @param slotNumber slot that should be cleared
	 * @return Event in which the unslot has been performed
	 */
	public EventRecipientApiDto unslot(long channel, int slotNumber) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		User userToUnslot = slot.getUser();
		slotService.unslot(slot, userToUnslot);
		return EventApiAssembler.toActionDto(event, userToUnslot);
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
		List<Squad> squad = event.getSquadList();
		if (squad.size() <= squadNumber) {
			throw BusinessRuntimeException.builder().title("Den Squad konnte ich nicht finden.").build();
		}

		squad.get(squadNumber).addSlot(slotService.newSlot(slotDto));

		return event;
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
		slotService.deleteSlot(slot);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and renames the slot by number.
	 *
	 * @param channel    event channel
	 * @param slotNumber to edit name of
	 * @param slotName   new name
	 * @return event in which the slot has been renamed
	 */
	public Event renameSlot(long channel, int slotNumber, String slotName) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		slotService.renameSlot(slot, slotName);
		return event;
	}

	/**
	 * Searches for the given channel the matching event, blocks the slot by number and sets the replacement text.
	 *
	 * @param channel         event channel
	 * @param slotNumber      to block
	 * @param replacementName text to be shown instead of user
	 * @return event in which the slot has been blocked
	 */
	public Event blockSlot(long channel, int slotNumber, String replacementName) {
		Event event = findByChannel(channel);
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		slotService.blockSlot(slot, replacementName);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and returns the slots matching the given slotNumber and user.
	 *
	 * @param channel    event channel
	 * @param slotNumber slot1 to find by slotNumber
	 * @param userDto    slot2 to find by user
	 * @return two Slots
	 */
	public List<Slot> findSwapSlots(long channel, int slotNumber, UserDto userDto) {
		Event event = findByChannel(channel);
		User user = userService.find(userDto);
		return Arrays.asList(
				event.findSlotOfUser(user).orElseThrow(ResourceNotFoundException::new),
				event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new)
		);
	}
}
