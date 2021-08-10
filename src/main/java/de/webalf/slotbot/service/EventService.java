package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.*;
import de.webalf.slotbot.model.dtos.api.EventRecipientApiDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.permissions.BotPermissionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.webalf.slotbot.util.EventUtils.assertApiAccessAllowed;

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
	private final EventTypeService eventTypeService;
	private final EventFieldService eventFieldService;
	private final EventDiscordInformationService eventDiscordInformationService;

	/**
	 * Creates a new event with values from the {@link EventDto}
	 *
	 * @param eventDto new event
	 * @return saved new event
	 */
	public Event createEvent(@NonNull EventDto eventDto) {
		final EventDiscordInformationDto discordInformation = eventDto.getDiscordInformation();
		if (discordInformation != null &&
				StringUtils.isNotEmpty(discordInformation.getChannel()) && findOptionalByChannel(discordInformation.getChannel()).isPresent()) {
			throw BusinessRuntimeException.builder().title("In diesem Kanal gibt es bereits ein Event.").build();
		}
		Event event = EventAssembler.fromDto(eventDto);
		event.setEventType(eventTypeService.find(eventDto.getEventType()));

		event.validate();

		event.setChilds();

		return eventRepository.save(event);
	}

	/**
	 * Returns an optional for the event associated with the given channelId
	 *
	 * @param channel to find event for
	 * @return Event found by channel or empty optional
	 */
	public Optional<Event> findOptionalByChannel(long channel) {
		return eventDiscordInformationService.findEventByChannel(channel);
	}

	/**
	 * Returns an optional for the event associated with the given channelId
	 *
	 * @param channel to find event for
	 * @return Event found by channel or empty optional
	 */
	private Optional<Event> findOptionalByChannel(@NotBlank String channel) {
		return eventDiscordInformationService.findEventByChannel(channel);
	}

	/**
	 * Returns the event associated with the given channelId
	 *
	 * @param channel to find event for
	 * @return Event from channel
	 * @throws ResourceNotFoundException if no event with this channelId could be found
	 */
	public Event findByChannel(long channel) {
		return findOptionalByChannel(channel).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Returns an optional for the event associated with the given eventId
	 *
	 * @param eventId to find event for
	 * @return Event found by id or empty optional
	 */
	public Optional<Event> findOptionalById(long eventId) {
		return eventRepository.findById(eventId);
	}

	/**
	 * Returns the event associated with the given eventId
	 *
	 * @param eventId to find event for
	 * @return Event found by id
	 * @throws ResourceNotFoundException if no event with this eventId could be found
	 */
	public Event findById(long eventId) {
		return findOptionalById(eventId).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * In addition to {@link #findById(long)}, the API access rights for the event are checked
	 */
	public Event findByIdForApi(long eventId) {
		final Event event = findById(eventId);
		assertApiAccessAllowed(event);
		return event;
	}

	/**
	 * Returns all events that take place in the specified period
	 *
	 * @return all events in given period
	 */
	public List<Event> findAllBetween(LocalDateTime start, LocalDateTime end) {
		if (BotPermissionHelper.hasEventManageRole()) {
			return eventRepository.findAllByDateTimeBetween(start, end);
		}
		return eventRepository.findAllByDateTimeBetweenAndHiddenFalse(start, end);
	}

	/**
	 * Returns all events that happened before now
	 *
	 * @return all events from the past
	 */
	public List<Event> findAllInPast() {
		return eventRepository.findAllByDateTimeIsBeforeAndOrderByDateTime(LocalDateTime.now());
	}

	/**
	 * Returns all events that are scheduled in the future and have no discord information
	 *
	 * @return all events in the future that have no channel
	 */
	public List<Event> findAllNotAssignedInFuture() {
		return eventRepository.findAllByDateTimeIsAfterAndOrderByDateTime(LocalDateTime.now());
	}

	/**
	 * Returns all {@link User}s slotted in the event associated with the given channelId.
	 * {@link User#DEFAULT_USER_ID} is filtered out.
	 *
	 * @param channel to find event for
	 * @return participant list
	 */
	public List<User> findAllParticipants(long channel) {
		return eventRepository.findAllParticipants(channel);
	}

	/**
	 * Updates the event found by id with values from the {@link AbstractEventDto}
	 *
	 * @param dto with event id and values to update
	 * @return updated event
	 */
	public Event updateEvent(@NonNull AbstractEventDto dto) {
		Event event = eventRepository.findById(dto.getId()).orElseThrow(ResourceNotFoundException::new);

		final EventDiscordInformationDto discordInformationDto = dto.getDiscordInformation();
		if (discordInformationDto != null) {
			final String channel = discordInformationDto.getChannel();
			if (StringUtils.isNotEmpty(channel)
					&& findOptionalByChannel(channel).filter(event1 -> !event1.equals(event)).isPresent()) {
				throw BusinessRuntimeException.builder().title("In diesem Kanal gibt es bereits ein Event.").build();
			}
		}


		DtoUtils.ifPresentObject(dto.getEventType(), eventType -> event.setEventType(eventTypeService.find(dto.getEventType())));
		DtoUtils.ifPresent(dto.getName(), event::setName);
		DtoUtils.ifPresent(dto.getDate(), event::setDate);
		DtoUtils.ifPresent(dto.getStartTime(), event::setTime);
		DtoUtils.ifPresent(dto.getCreator(), event::setCreator);
		DtoUtils.ifPresent(dto.isHidden(), event::setHidden);
		DtoUtils.ifPresentOrEmpty(dto.getDescription(), event::setDescription);
		DtoUtils.ifPresentOrEmpty(dto.getRawPictureUrl(), event::setPictureUrl);
		DtoUtils.ifPresentOrEmpty(dto.getMissionType(), event::setMissionType);
		DtoUtils.ifPresentOrEmpty(dto.getMissionLength(), event::setMissionLength);
		DtoUtils.ifPresent(dto.getReserveParticipating(), event::setReserveParticipating);
		DtoUtils.ifPresentObject(discordInformationDto, discordInformation ->
				event.setDiscordInformation(eventDiscordInformationService.updateOrCreateDiscordInformation(discordInformation, event)));

		return event;
	}

	/**
	 * Updates the event found by id with values from the {@link EventDto}
	 *
	 * @param dto with event id and values to update
	 * @return updated event
	 * @see #updateEvent(AbstractEventDto)
	 */
	public Event updateEvent(@NonNull EventDto dto) {
		Event event = updateEvent((AbstractEventDto) dto);

		if (dto.getSquadList() != null) {
			squadService.updateSquadList(dto.getSquadList(), event);
		}
		if (dto.getDetails() != null) {
			eventFieldService.updateEventDetails(dto.getDetails(), event);
		}

		return event;
	}

	/**
	 * Searches for the given event by id and archives it
	 *
	 * @param eventId event id
	 */
	public void archiveEvent(long eventId) {
		findById(eventId).archive();
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
		event.unslotIfAlreadySlotted(user);
		eventRepository.saveAndFlush(event);
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
	 * Searches for the given channel the matching event and enters the given user for a random empty slot, if available.
	 *
	 * @param channel event channel
	 * @param userDto person that should be slotted
	 * @return Event in which the person has been slotted
	 * @throws BusinessRuntimeException if no slot is available
	 */
	public Event randomSlot(long channel, UserDto userDto) {
		Event event = findByChannel(channel);
		Slot slot = event.randomSlot();
		User user = userService.find(userDto);
		slotService.slot(slot, user);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and renames the squad by position.
	 *
	 * @param channel       event channel
	 * @param squadPosition to edit name of
	 * @param squadName     new name
	 * @return event in which the slot has been renamed
	 */
	public Event renameSquad(long channel, int squadPosition, String squadName) {
		final Event event = findByChannel(channel);
		event.findSquadByPosition(squadPosition).setName(squadName);
		return event;
	}

	/**
	 * Searches for the given channel the matching event and adds the given slot to the squad found by squadPosition.
	 *
	 * @param channel       event channel
	 * @param squadPosition Counted, starting by 0
	 * @param slotDto       slot to add
	 * @return event in which the slot has been added
	 */
	public Event addSlot(long channel, int squadPosition, SlotDto slotDto) {
		final Event event = findByChannel(channel);
		final Squad squad = event.findSquadByPosition(squadPosition);
		if (squad.isReserve()) {
			throw BusinessRuntimeException.builder().title("Der Reserve kann manuell kein Slot hinzugefügt werden.").build();
		}
		squad.addSlot(slotService.newSlot(slotDto));
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
	 * Searches for the given channel the matching event and returns the slots matching the two given users.
	 *
	 * @param channel  event channel
	 * @param userDtos slotted users
	 * @return two slots
	 */
	public List<Slot> findSwapSlots(long channel, List<UserDto> userDtos) {
		if (ListUtils.isEmpty(userDtos) || userDtos.size() != 2) {
			throw BusinessRuntimeException.builder().title("Zum tauschen müssen zwei Nutzer angegeben werden.").build();
		}

		Event event = findByChannel(channel);

		ArrayList<Slot> slots = new ArrayList<>();
		userDtos.forEach(userDto -> slots.add(event.findSlotOfUser(userService.find(userDto)).orElseThrow(ResourceNotFoundException::new)));
		return slots;
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

		Slot slotFoundByNumber = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		if (slotFoundByNumber.getUser() != null && slotFoundByNumber.getUser().isDefaultUser()) {
			throw BusinessRuntimeException.builder().title("Mit einem gesperrten Slot kann nicht getauscht werden.").build();
		}

		return Arrays.asList(
				event.findSlotOfUser(user).orElseThrow(ResourceNotFoundException::new),
				slotFoundByNumber
		);
	}
}
