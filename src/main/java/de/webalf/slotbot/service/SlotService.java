package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.SlotAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.exception.SlottableException;
import de.webalf.slotbot.feature.action_log.ActionLogService;
import de.webalf.slotbot.feature.action_log.model.LogAction;
import de.webalf.slotbot.feature.requirement.RequirementService;
import de.webalf.slotbot.feature.slot_rules.Slottable;
import de.webalf.slotbot.model.*;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.website.event.edit.MinimalSlotIdDto;
import de.webalf.slotbot.model.enums.SlottableState;
import de.webalf.slotbot.model.event.BanEvent;
import de.webalf.slotbot.repository.SlotRepository;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static de.webalf.slotbot.model.enums.SlottableState.*;

/**
 * @author Alf
 * @since 27.07.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SlotService {
	private final SlotRepository slotRepository;
	private final GuildService guildService;
	private final RequirementService requirementService;
	private final UserService userService;
	private final ActionLogService actionLogService;
	private final BanService banService;

	/**
	 * Returns the slot with the given id
	 *
	 * @param id of the slot to get
	 * @return slot
	 * @throws ResourceNotFoundException if no slot with the given id exists
	 */
	Slot findById(long id) {
		return slotRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Returns the slot of the given user in the given event.
	 *
	 * @param user  user to find slot for
	 * @param event event in which the user may be slotted
	 * @return slot in the event in which the user is slotted
	 * @throws ResourceNotFoundException if no slot matching the user and event is found
	 */
	Slot findByUserAndEvent(@NonNull User user, @NonNull Event event) {
		return slotRepository.findByUserAndSquadEvent(user, event).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Creates a new {@link Slot} with the values from the given {@link SlotDto}
	 *
	 * @param dto values
	 * @return saved new slot
	 */
	Slot newSlot(SlotDto dto) {
		Slot slot = SlotAssembler.fromDto(dto);
		return slotRepository.save(slot);
	}

	/**
	 * Updates the slotlist of the given squad to the given slotlist
	 *
	 * @param slotList new slotlist
	 * @param squad    to update
	 */
	void updateSlotList(@NonNull List<MinimalSlotIdDto> slotList, @NonNull Squad squad) {
		final List<Long> retainedSlotIds = slotList.stream().map(MinimalSlotIdDto::getId).filter(id -> id != 0).toList();
		if (squad.getSlotList() == null) {
			squad.setSlotList(new ArrayList<>());
		}
		final List<Slot> squadSlots = squad.getSlotList();
		//Remove slots that are not in the new list
		squadSlots.removeIf(slot -> !retainedSlotIds.contains(slot.getId()));

		//For each new slot, find the matching existing slot by id, if not found create a new one
		slotList.forEach(slotDto -> {
			final Slot slot = squadSlots.stream()
					.filter(s -> s.getId() == slotDto.getId() && slotDto.getId() != 0)
					.findAny()
					.orElseGet(() -> {
						final Slot newSlot = Slot.builder().squad(squad).build();
						squadSlots.add(newSlot);
						return newSlot;
					});
			updateSlot(slotDto, slot);
		});
	}

	/**
	 * Updates the given slot with the values from the given dto
	 * (!) Squad can not be changed
	 *
	 * @param dto  new values
	 * @param slot to update
	 */
	private void updateSlot(@NonNull MinimalSlotIdDto dto, @NonNull Slot slot) {
		DtoUtils.ifPresent(dto.getName(), slot::setName);
		DtoUtils.ifPresent(dto.getNumber(), slot::setNumber);
		slot.setReservedFor(guildService.evaluateReservedFor(dto.getReservedFor()));
		DtoUtils.ifPresentObject(dto.getRequirements(), requirements -> slot.setRequirements(requirementService.find(requirements)));
		if (dto.isBlocked()) {
			final String replacementText = dto.getReplacementText();
			blockSlot(slot, StringUtils.isNotEmpty(replacementText) ? replacementText.trim() : null);
		} else if (slot.isBlocked()) {
			slot.setUser(null);
			slot.setReplacementText(null);
		}
	}

	/**
	 * Determines the {@link Slottable} state of the given slot for the currently logged-in user
	 *
	 * @param slot to check
	 * @return state of the slot for the user
	 */
	public Slottable getSlottable(@NonNull Slot slot) {
		final User loggedIn = userService.getPotentialLoggedIn();
		if (loggedIn == null) {
			return new Slottable(NOT_AVAILABLE);
		}
		return getSlottable(slot, loggedIn);
	}

	/**
	 * Determines the {@link Slottable} state of the given slot for the given user
	 *
	 * @param slot to check
	 * @param user to slot
	 * @return state of the slot for the user
	 */
	Slottable getSlottable(@NonNull Slot slot, @NonNull User user) {
		final Event slotEvent = slot.getEvent();
		if (DateUtils.isMoreThan24HoursAgo(slotEvent.getDateTime())) {
			return new Slottable(NOT_AVAILABLE);
		}
		final Slottable slottable = slot.getSlottable(user);
		if (slottable.state().isSlottingAllowed() && banService.isBanned(user, slotEvent.getOwnerGuild(), slot.getEffectiveReservedFor())) {
			return new Slottable(NO_BANNED);
		}
		return slottable;
	}

	boolean isSlottable(@NonNull Slot slot, @NonNull User user) {
		return getSlottable(slot, user).state().isSlottingAllowed();
	}

	/**
	 * Validates the availability of the given slot for the given user.
	 *
	 * @param slot to check
	 * @param user to slot
	 * @throws SlottableException if the slot is not available for the user
	 */
	void assertSlotIsPossible(@NonNull Slot slot, @NonNull User user) {
		final Slottable slottable = getSlottable(slot, user);
		final SlottableState slottableState = slottable.state();
		if (YES.equals(slottableState) || YES_REQUIREMENTS_NOT_MET.equals(slottableState)) {
			return;
		}
		throw SlottableException.builder().slottable(slottable).build();
	}

	/**
	 * Slots the given user to the given slot.
	 * <p>
	 * All {@link #assertSlotIsPossible(Slot, User) prerequisites should be checked} before calling this method.
	 * <p>
	 * If the user is already slotted, an {@link Event#unslotIfAlreadySlotted(User) unslot} should be performed and
	 * persisted first to ensure notification order.
	 *
	 * @param slotId id of slot in which slot should be performed
	 * @param user   to be slotted
	 * @return the updated slot
	 */
	Slot slot(long slotId, @NonNull User user) {
		final Slot slot = findById(slotId);
		slot.slot(user);
		actionLogService.logEventAction(LogAction.SLOT, slot.getEvent(), user);
		return slot;
	}

	/**
	 * Removes the given user from the given slot
	 *
	 * @param slot in which unslot should be performed
	 * @param user to be unslotted
	 */
	void unslot(@NonNull Slot slot, User user) {
		if (user == null) {
			throw BusinessRuntimeException.builder().title("Einen leeren Slot brauchst du nicht ausslotten").build();
		}
		slot.unslot(user);
		actionLogService.logEventAction(LogAction.UNSLOT, slot.getEvent(), user);
	}

	/**
	 * Renames the slot with the given name for the specified event and slot number.
	 *
	 * @param event      event to which the slot belongs
	 * @param slotNumber slot number
	 * @param slotName   new slot name
	 */
	public void renameSlot(@NonNull Event event, int slotNumber, @NotBlank String slotName) {
		final Slot slot = slotRepository.findByNumberAndSquadEvent(slotNumber, event).orElseThrow(ResourceNotFoundException::new);
		slot.setName(slotName);
	}

	/**
	 * Removes the slot with the given slot number from the given event.
	 *
	 * @param event      event to which the slot belongs
	 * @param slotNumber slot number
	 */
	public void deleteSlot(@NonNull Event event, int slotNumber) {
		final Slot slot = slotRepository.findByNumberAndSquadEvent(slotNumber, event).orElseThrow(ResourceNotFoundException::new);
		slot.getSquad().deleteSlot(slot);
	}

	/**
	 * Blocks the given slot and sets the replacement text
	 *
	 * @param slot            to block
	 * @param replacementText to be shown instead the user
	 */
	void blockSlot(@NonNull Slot slot, String replacementText) {
		slot.blockSlot(userService.find(User.DEFAULT_USER_ID), replacementText);
	}

	/**
	 * Swaps the users of the given slots
	 *
	 * @return the event from the second slot
	 */
	public Event swap(@NonNull Slot slot1, @NonNull Slot slot2) {
		slot1.swapUsers(slot2);

		actionLogService.logEventAction(LogAction.SWAP, slot1.getEvent(), slot1.getUser(), slot2.getUser());

		return slot1.getEvent();
	}

	/**
	 * Counts the number of slots of the given user before today
	 *
	 * @param user to count slots for
	 * @return number of participations
	 */
	public long countByUserBeforeToday(@NonNull User user) {
		return slotRepository.countByUserAndSquadEventDateTimeBefore(user, DateUtils.now());
	}

	@EventListener
	@Async
	public void onBanEvent(@NonNull BanEvent event) {
		final User user = userService.findExisting(event.userId());
		final List<Slot> slotsToBeEmptied;
		if (event.guildId() == null) {
			slotsToBeEmptied = slotRepository.findByUserAndEventAfter(user, DateUtils.now());
		} else {
			final Guild guild = guildService.findExisting(event.guildId());
			slotsToBeEmptied = slotRepository.findByUserAndForGuildAndEventAfter(user, guild, DateUtils.now());
		}
		slotsToBeEmptied.forEach(slot -> unslot(slot, user));
	}
}
