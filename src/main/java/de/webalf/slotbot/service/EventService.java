package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.website.event.creation.EventPostAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.*;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.dtos.website.event.creation.EventPostDto;
import de.webalf.slotbot.model.dtos.website.event.edit.EventUpdateDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.hasEventManageRole;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final EventPostAssembler eventPostAssembler;
	private final SquadService squadService;
	private final SlotService slotService;
	private final UserService userService;
	private final EventTypeService eventTypeService;
	private final EventFieldService eventFieldService;
	private final EventDiscordInformationService eventDiscordInformationService;
	private final GuildService guildService;

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
	 * Returns the event associated with the given eventId
	 *
	 * @param eventId to find event for
	 * @return Event found by id
	 * @throws ResourceNotFoundException if no event with this eventId could be found
	 */
	public Event findById(long eventId) {
		return eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
	}

	public List<Event> findAllBetween(LocalDateTime start, LocalDateTime end) {
		return findAllBetween(start, end, hasEventManageRole());
	}

	/**
	 * Returns all events in the given period.
	 * Events are filtered by the {@link GuildService#findCurrentNonNullGuild() current guild}.
	 *
	 * @return all events in given period
	 */
	public List<Event> findAllBetween(LocalDateTime start, LocalDateTime end, boolean canReadHidden) {
		final Guild ownerGuild = guildService.findCurrentNonNullGuild();
		if (ownerGuild.getId() == GUILD_PLACEHOLDER) {
			return canReadHidden ?
					eventRepository.findAllByDateTimeBetweenAndShareableTrueOrPlaceholderGuild(start, end) :
					eventRepository.findAllByDateTimeBetweenAndHiddenFalseAndShareableTrueOrPlaceholderGuild(start, end);
		}

		return canReadHidden ?
				eventRepository.findAllByGuildAndDateTimeBetween(ownerGuild, start, end) :
				eventRepository.findAllByGuildAndDateTimeBetweenAndHiddenFalse(ownerGuild, start, end);
	}

	public List<Event> findAllPublicByGuild(Guild ownerGuild) {
		return eventRepository.findAllByGuildAndHiddenFalse(ownerGuild);
	}

	/**
	 * Returns all events for the given guild that happened before now
	 *
	 * @return all events from the past
	 */
	public List<Event> findAllInPast(Guild guild) {
		return eventRepository.findAllByDateTimeIsBeforeAndOwnerGuildAndOrderByDateTime(DateUtils.now(), guild);
	}

	/**
	 * Returns all events that are scheduled after now
	 *
	 * @return all events from the future
	 */
	public List<Event> findAllInFuture() {
		return eventRepository.findByDateTimeGreaterThan(DateUtils.now());
	}

	/**
	 * Returns all events of the given owner guild that are scheduled in the future and have no discord information
	 *
	 * @param guildId to find events for
	 * @return all events in the future that have no channel
	 */
	public List<Event> findAllNotAssignedInFuture(long guildId) {
		return eventRepository.findAllByDateTimeIsAfterAndNotScheduledAndOwnerGuildAndForGuildAndOrderByDateTime(DateUtils.now(), guildId);
	}

	/**
	 * Returns all events that the given guild is not owner of, that are scheduled in the future and have no discord information
	 *
	 * @param guildId to exclude as owner guild
	 * @return all events in the future that have no channel
	 */
	public List<Event> findAllForeignNotAssignedInFuture(long guildId) {
		return eventRepository.findAllByDateTimeIsAfterAndNotScheduledAndNotOwnerGuildAndForGuildAndOrderByDateTime(DateUtils.now(), guildId);
	}

	/**
	 * Returns all ids of the {@link User}s slotted in the event associated with the given channelId.
	 * {@link User#DEFAULT_USER_ID} is filtered out.
	 *
	 * @param channel to find event for
	 * @return participant list
	 */
	public List<Long> findAllParticipantIds(long channel) {
		return eventRepository.findAllParticipantIds(channel);
	}

	/**
	 * Returns the owner guild of the event found by its id.
	 *
	 * @param eventId to find owner guild of
	 * @return owner guild
	 */
	public Guild getGuildByEventId(long eventId) {
		return eventRepository.findOwnerGuildById(eventId).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * {@link Event#validate() Validates} the given event and saves it.
	 *
	 * @param event to save
	 * @return saved event
	 */
	public Event save(@NonNull Event event) {
		event.validate();

		return eventRepository.save(event);
	}

	/**
	 * Creates a new event with values from the {@link EventDto}
	 * {@link Event#setOwnerGuild(Guild)} is set by current request uri ({@link GuildService#findCurrentNonNullGuild()})
	 *
	 * @param eventDto new event
	 * @return saved new event
	 */
	public Event createEvent(@NonNull EventPostDto eventDto) {
		return save(eventPostAssembler.fromDto(eventDto));
	}

	/**
	 * Updates the event found by id with values from the {@link EventDto}.
	 * <p>
	 * For updating discord information see {@link #addDiscordInformation(long, EventDiscordInformationDto)}
	 *
	 * @param eventId event to update
	 * @param dto     with values to update
	 * @return updated event
	 */
	public Event updateEvent(long eventId, @NonNull EventUpdateDto dto) {
		Event event = findById(eventId);

		DtoUtils.ifPresent(dto.getHidden(), event::setHidden);
		DtoUtils.ifPresent(dto.getShareable(), event::setShareable);
		DtoUtils.ifPresent(dto.getName(), event::setName);
		DtoUtils.ifPresent(dto.getDateTime(), event::setDateTime);
		DtoUtils.ifPresent(dto.getCreator(), event::setCreator);
		DtoUtils.ifPresentObject(dto.getEventType(), eventType -> event.setEventType(eventTypeService.find(dto.getEventType(), event.getOwnerGuild())));
		DtoUtils.ifPresentOrEmpty(dto.getDescription(), event::setDescription);
		DtoUtils.ifPresentOrEmpty(dto.getMissionType(), event::setMissionType);
		DtoUtils.ifPresentOrEmpty(dto.getMissionLength(), event::setMissionLength);
		DtoUtils.ifPresentOrEmpty(dto.getPictureUrl(), pictureUrl -> {
			if (!EmbedBuilder.URL_PATTERN.matcher(pictureUrl).matches()) {
				throw BusinessRuntimeException.builder().title("Invalid picture url pattern").build();
			}
			event.setPictureUrl(pictureUrl);
		});
		DtoUtils.ifPresent(dto.getReserveParticipating(), event::setReserveParticipating);

		DtoUtils.ifPresentObject(dto.getDetails(), details -> eventFieldService.updateEventDetails(details, event));
		DtoUtils.ifPresentObject(dto.getSquadList(), squadlist -> {
			squadService.updateSquadList(squadlist, event);
			event.removeReservedForDefaultGuild();
		});

		return event;
	}

	/**
	 * Adds the discord information of the event found by id with values from the {@link EventDiscordInformationDto}.
	 *
	 * @param eventId event to update
	 * @param dto     new discord information
	 * @return updated event
	 */
	public Event addDiscordInformation(long eventId, @NonNull EventDiscordInformationDto dto) {
		final Event event = findById(eventId);
		eventDiscordInformationService.updateDiscordInformation(Set.of(dto), event);
		return event;
	}

	/**
	 * Deletes the given event
	 */
	public void deleteEvent(Event event) {
		eventRepository.delete(event);
	}

	/**
	 * Enters the given user for the slot with given number in given event, if available.
	 *
	 * @param event      event
	 * @param slotNumber Slot to slot into
	 * @param userId     person that should be slotted
	 * @return Event in which the person has been slotted
	 * @throws BusinessRuntimeException if the slot is already occupied
	 */
	public Event slot(@NonNull Event event, int slotNumber, long userId) {
		final Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		final User user = userService.find(userId);
		return slot(event, slot, user);
	}

	/**
	 * Slots the {@link UserService#getLoggedIn() logged-in user} into the slot found by given id
	 */
	public Event slot(long slotId) {
		final Slot slot = slotService.findById(slotId);
		return slot(slot.getEvent(), slot, userService.getLoggedIn());
	}

	/**
	 * Slots the given user into the given slot of the given event. Checks slotting permissions and ensures unslot
	 * before slotting.
	 *
	 * @param event event
	 * @param slot  slot to slot into
	 * @param user  user to slot
	 * @return event in which the person has been slotted
	 */
	private Event slot(@NonNull Event event, @NonNull Slot slot, @NonNull User user) {
		slot.assertSlotIsPossible(user);
		event.unslotIfAlreadySlotted(user);
		eventRepository.saveAndFlush(event);
		slotService.slot(slot, user);
		return event;
	}

	/**
	 * Blocks the slot by number and sets the replacement text in the given event.
	 *
	 * @param event           event
	 * @param slotNumber      to block
	 * @param replacementName text to be shown instead of user
	 * @return event in which the slot has been blocked
	 */
	public Event blockSlot(@NonNull Event event, int slotNumber, String replacementName) {
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		slotService.blockSlot(slot, replacementName);
		return event;
	}

	/**
	 * Removes the user, found by userDto, from its slot in given event.
	 *
	 * @param event   event
	 * @param userDto person that should be unslotted
	 * @return Event in which the person has been unslotted
	 * @throws ResourceNotFoundException if the user is not slotted in the given event
	 */
	public Event unslot(@NonNull Event event, UserDto userDto) {
		final User user = userService.find(userDto);
		final Slot slot = event.findSlotOfUser(user).orElseThrow(ResourceNotFoundException::new);
		return unslot(event, slot, user);
	}

	/**
	 * Removes the {@link UserService#getLoggedIn() logged-in user} from the slot found by given id
	 */
	public Event unslot(long slotId) {
		final Slot slot = slotService.findById(slotId);
		return unslot(slot.getEvent(), slot, userService.getLoggedIn());
	}

	/**
	 * Removes the user, found by slotNumber, from its slot in the given event.
	 *
	 * @param event      event
	 * @param slotNumber slot that should be cleared
	 * @return event in which the unslot has been performed
	 */
	public Event unslot(@NonNull Event event, int slotNumber) {
		final Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		final User userToUnslot = slot.getUser();
		return unslot(event, slot, userToUnslot);
	}

	/**
	 * Removes the given user from its slot in the given event.
	 *
	 * @param event event
	 * @param slot  slot to unslot from
	 * @param user  user to unslot
	 * @return event in which the unslot has been performed
	 */
	private Event unslot(@NonNull Event event, @NonNull Slot slot, User user) {
		slotService.unslot(slot, user);
		return event;
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
		User user = userService.find(userDto);
		Slot slot = event.randomSlot(user);
		slotService.slot(slot, user);
		return event;
	}

	/**
	 * Renames the squad by position in the given event.
	 *
	 * @param event         event
	 * @param squadPosition to edit name of
	 * @param squadName     new name
	 * @return event in which the slot has been renamed
	 */
	public Event renameSquad(@NonNull Event event, int squadPosition, String squadName) {
		final Squad squad = event.findSquadByPosition(squadPosition);
		if (squad.isReserve()) {
			throw new ForbiddenException("Reserve may not be renamed.");
		}
		squad.setName(squadName);
		return event;
	}

	/**
	 * Adds the given slot to the squad found by squadPosition in the given event.
	 *
	 * @param event         event
	 * @param squadPosition Counted, starting by 0
	 * @param slotDto       slot to add
	 * @return event in which the slot has been added
	 */
	public Event addSlot(@NonNull Event event, int squadPosition, SlotDto slotDto) {
		final Squad squad = event.findSquadByPosition(squadPosition);
		squad.addSlot(slotService.newSlot(slotDto));
		return event;
	}

	/**
	 * Removes the slot by number in the given event.
	 *
	 * @param event      event
	 * @param slotNumber to delete
	 * @return event in which the slot has been deleted
	 */
	public Event deleteSlot(@NonNull Event event, int slotNumber) {
		Slot slot = event.findSlot(slotNumber).orElseThrow(ResourceNotFoundException::new);
		slotService.deleteSlot(slot);
		return event;
	}

	/**
	 * Renames the slot by number in the given event.
	 *
	 * @param event      event
	 * @param slotNumber to edit name of
	 * @param slotName   new name
	 */
	public void renameSlot(@NonNull Event event, int slotNumber, String slotName) {
		slotService.renameSlot(event, slotNumber, slotName);
	}

	/**
	 * Returns the slots matching the two given users in the given event.
	 *
	 * @param event    event
	 * @param userDtos slotted users
	 * @return two slots
	 */
	public List<Slot> findSwapSlots(@NonNull Event event, List<UserDto> userDtos) {
		if (org.springframework.util.CollectionUtils.isEmpty(userDtos) || userDtos.size() != 2) {
			throw BusinessRuntimeException.builder().title("Zum tauschen müssen zwei Nutzer angegeben werden.").build();
		}

		ArrayList<Slot> slots = new ArrayList<>();
		userDtos.forEach(userDto -> slots.add(event.findSlotOfUser(userService.find(userDto)).orElseThrow(ResourceNotFoundException::new)));
		return slots;
	}
}
