package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.website.event.creation.EventPostAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
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
import de.webalf.slotbot.util.EventUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.permissions.PermissionHelper.hasEventManagePermission;

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
		return findAllBetween(start, end, hasEventManagePermission(guildService.getCurrentGuildId()));
	}

	/**
	 * Returns all events in the given period.
	 * Events are filtered by the {@link GuildService#findCurrentNonNullGuild() current guild}.
	 *
	 * @return all events in given period
	 */
	public List<Event> findAllBetween(LocalDateTime start, LocalDateTime end, boolean canReadHidden) {
		final Guild ownerGuild = guildService.findCurrentNonNullGuild();
		return canReadHidden ?
				findAllByDateTimeBetween(ownerGuild, start, end) :
				findAllByDateTimeBetweenAndHiddenFalse(ownerGuild, start, end, null);
	}

	/**
	 * Returns the two most recent events and up to 15 upcoming events. Only 30 days in the past and future are considered.
	 *
	 * @return list of events around today
	 */
	public List<Event> findAllAroundToday() {
		final Guild ownerGuild = guildService.findCurrentNonNullGuild();

		final LocalDateTime now = DateUtils.now();
		final LocalDateTime pastStartDateTime = now.minusDays(30);
		final LocalDateTime futureEndDateTime = now.plusDays(30);

		// Fetch the two most recent events from the past 30 days
		final List<Event> recentEvents = findAllByDateTimeBetweenAndHiddenFalse(ownerGuild, pastStartDateTime, now, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, Event_.DATE_TIME)));
		// Fetch up to 15 upcoming events in the next 30 days
		final List<Event> upcomingEvents = findAllByDateTimeBetweenAndHiddenFalse(ownerGuild, now, futureEndDateTime, PageRequest.of(0, 15, Sort.by(Sort.Direction.ASC, Event_.DATE_TIME)));

		return Stream.concat(recentEvents.reversed().stream(), upcomingEvents.stream()).toList();
	}

	/**
	 * Returns all publicly visible events of the given guild in the given period.
	 * For the placeholder guild all shareable events and public events are returned.
	 *
	 * @return all events in given period
	 * @see #findAllByDateTimeBetween(Guild, LocalDateTime, LocalDateTime)
	 */
	private List<Event> findAllByDateTimeBetweenAndHiddenFalse(@NonNull Guild ownerGuild, LocalDateTime start, LocalDateTime end, Pageable pageable) {
		return ownerGuild.getId() == GUILD_PLACEHOLDER ?
				eventRepository.findAllByDateTimeBetweenAndHiddenFalseAndShareableTrueOrPlaceholderGuild(start, end, pageable) :
				eventRepository.findAllByGuildAndDateTimeBetweenAndHiddenFalse(ownerGuild, start, end, pageable);
	}

	/**
	 * Returns all events of the given guild in the given period.
	 * For the placeholder guild all shareable events and public events are returned.
	 *
	 * @return all events in given period
	 * @see #findAllByDateTimeBetweenAndHiddenFalse(Guild, LocalDateTime, LocalDateTime, Pageable)
	 */
	private List<Event> findAllByDateTimeBetween(@NonNull Guild ownerGuild, LocalDateTime start, LocalDateTime end) {
		return ownerGuild.getId() == GUILD_PLACEHOLDER ?
				eventRepository.findAllByDateTimeBetweenAndShareableTrueOrPlaceholderGuild(start, end) :
				eventRepository.findAllByGuildAndDateTimeBetween(ownerGuild, start, end);
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
	 * Searches for the last event of the given guild the given user is slotted in.
	 *
	 * @param user       user to search for
	 * @param ownerGuild event owner guild
	 * @return Optional containing the last event of the user, or empty if no event is found
	 */
	public Optional<Event> findLastEventOfUserInGuild(@NonNull User user, @NonNull Guild ownerGuild) {
		return eventRepository.findFirstByOwnerGuildAndSquadListSlotListUserOrderByDateTimeDesc(ownerGuild, user);
	}

	public List<Event> findEventsOfUser(@NonNull User user) {
		return eventRepository.findBySquadListSlotListUser(user);
	}

	/**
	 * Searches for the last event before today the given user was slotted in.
	 *
	 * @param user to search for
	 * @return Optional containing the last event of the user, or empty if no event is found
	 */
	public Optional<Event> findLastEvent(@NonNull User user) {
		return eventRepository.findFirstBySquadList_SlotList_UserAndDateTimeBeforeOrderByDateTimeDesc(user, DateUtils.now());
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
		DtoUtils.ifPresentOrEmpty(EventUtils.sanitize(dto.getDescription()), event::setDescription);
		DtoUtils.ifPresentOrEmpty(dto.getMissionType(), event::setMissionType);
		DtoUtils.ifPresentOrEmpty(dto.getMissionLength(), event::setMissionLength);
		DtoUtils.ifPresentOrEmpty(dto.getPictureUrl(), pictureUrl -> {
			if (pictureUrl != null && !EmbedBuilder.URL_PATTERN.matcher(pictureUrl).matches()) {
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
		//After the event was flushed the old slot can't be reused anymore
		slotService.slot(slot.getId(), user);
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
		return slot(event, slot, user);
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
		squad.setName(squadName);
		return event;
	}

	/**
	 * Adds a slot to the squad found by squadPosition in the given event.
	 *
	 * @param event         event
	 * @param squadPosition Counted, starting by 0
	 * @param slotNumber    optional number of new slot
	 * @param slotName      name of new slot
	 */
	public void addSlot(@NonNull Event event, int squadPosition, Integer slotNumber, String slotName) {
		final Squad squad = event.findSquadByPosition(squadPosition);
		squad.addSlot(slotService
				.newSlot(SlotDto.builder()
						.number(slotNumber != null ? slotNumber : event.findFirstUnusedSlotNumber())
						.name(slotName)
						.build()));
	}

	/**
	 * Removes the slot by number in the given event.
	 *
	 * @param event      event
	 * @param slotNumber to delete
	 */
	public void deleteSlot(@NonNull Event event, int slotNumber) {
		slotService.deleteSlot(event, slotNumber);
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
}
