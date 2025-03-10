package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.event.EventArchiveEvent;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.model.Squad.RESERVE_NAME;
import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "event", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Slf4j
public class Event extends AbstractSuperIdEntity {
	@Column(name = "event_hidden")
	@Builder.Default
	private boolean hidden = false;

	@Column(name = "event_shareable")
	@Builder.Default
	private boolean shareable = false;

	@Column(name = "event_name", length = TEXT_DB, nullable = false)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@Column(name = "event_date")
	@NotNull
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime dateTime;

	@Column(name = "event_creator", length = TEXT_DB, nullable = false)
	@NotBlank
	@Size(max = TEXT)
	private String creator;

	@ManyToOne
	@JoinColumn(name = "event_type")
	@NotNull
	private EventType eventType;

	@Column(name = "event_description", columnDefinition = "text")
	private String description;

	@Column(name = "event_mission_type", length = TEXT_DB)
	@Size(max = TEXT)
	private String missionType;

	@Column(name = "event_mission_length", length = TEXT_DB)
	@Size(max = TEXT)
	private String missionLength;

	@Column(name = "event_picture_url", length = URL_DB)
	@Size(max = URL)
	@Pattern(regexp = URL_PATTERN)
	private String pictureUrl;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<EventField> details;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<Squad> squadList;

	@ManyToMany
	@JoinTable(name = "event_requirement",
			joinColumns = @JoinColumn(name = "event_id", foreignKey = @ForeignKey(name = "event_fk")),
			inverseJoinColumns = @JoinColumn(name = "requirement_id", foreignKey = @ForeignKey(name = "requirement_fk")))
	private Set<Requirement> requirements;

	@Column(name = "event_reserve_participating")
	private Boolean reserveParticipating;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private Set<EventDiscordInformation> discordInformation;

	@ManyToOne(targetEntity = Guild.class, optional = false)
	@JoinColumn(name = "event_owner_guild")
	private Guild ownerGuild;

	// Getter

	/**
	 * Returns the picture url or the guild logo if no picture url is set
	 *
	 * @return not empty picture url
	 */
	public String getPictureUrlWithDefault() {
		return StringUtils.isNotEmpty(pictureUrl) ? pictureUrl : GuildService.getLogo(getOwnerGuild());
	}

	/**
	 * Finds a {@link Squad} by its position in an event. Reserve is not included.
	 *
	 * @param squadPosition position to get squad for
	 * @return the squad
	 * @throws BusinessRuntimeException if there is no squad at the given position
	 */
	public Squad findSquadByPosition(int squadPosition) {
		final List<Squad> squad = getSquadsExceptReserve();
		if (squad.size() <= squadPosition || squadPosition < 0) {
			throw BusinessRuntimeException.builder().title("Couldn't find a squad on position " + squadPosition + ".").build();
		}
		return squad.get(squadPosition);
	}

	/**
	 * Finds a slot by its number
	 *
	 * @param slotNumber associated to the slot
	 * @return the slot or an empty Optional if slot with given number doesn't exist
	 */
	public Optional<Slot> findSlot(int slotNumber) {
		for (Squad squad : getSquadList()) {
			Optional<Slot> slotOptional = squad.findSlot(slotNumber);
			if (slotOptional.isPresent()) {
				return slotOptional;
			}
		}
		return Optional.empty();
	}

	/**
	 * Finds a slot by its user
	 *
	 * @param user associated to the slot
	 * @return the slot or an empty Optional if slot with given user doesn't exist
	 */
	public Optional<Slot> findSlotOfUser(User user) {
		for (Squad squad : getSquadList()) {
			Optional<Slot> slotOptional = squad.findSlotOfUser(user);
			if (slotOptional.isPresent()) {
				return slotOptional;
			}
		}
		return Optional.empty();
	}

	private Optional<Squad> findSquadByName(String name) {
		for (Squad squad : getSquadList()) {
			if (squad.getName().equalsIgnoreCase(name)) {
				return Optional.of(squad);
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns all usable squads. This excludes the reserve
	 *
	 * @return every squad that isn't the reserve
	 */
	private List<Squad> getSquadsExceptReserve() {
		return getSquadList().stream().filter(squad -> !squad.isReserve()).toList();
	}

	public Set<User> getAllParticipants() {
		return getSquadList().stream()
				.flatMap(squad -> squad.getSlotList().stream()
						.map(Slot::getUser).filter(Objects::nonNull))
				.collect(Collectors.toUnmodifiableSet());
	}

	private boolean isFull() {
		for (Squad squad : getSquadsExceptReserve()) {
			if (squad.getSlotList().stream().anyMatch(Slot::isEmpty)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the entire slot list is empty
	 *
	 * @return {@code true} if no one is slotted otherwise {@code false}
	 */
	public boolean isEmpty() {
		return getSquadList().stream()
				.flatMap(squad -> squad.getSlotList().stream())
				.noneMatch(Slot::isNotEmpty);
	}

	/**
	 * @return slot count excluding reserve
	 */
	public int getSlotCountWithoutReserve() {
		int slotCount = 0;
		for (Squad squad : getSquadsExceptReserve()) {
			slotCount += squad.getSlotList().size();
		}
		return slotCount;
	}

	/**
	 * Returns 25% of the slot counts but at least one
	 *
	 * @return how big the reserve squad should be
	 */
	private int getDesiredReserveSize() {
		int slotCount = getSlotCountWithoutReserve();
		return slotCount < 4 ? 1 : (int) Math.ceil(slotCount / 4.);
	}

	/**
	 * Finds the first unused slot number among all squads.
	 *
	 * @return the first unused slot number, starting from 1
	 */
	public int findFirstUnusedSlotNumber() {
		final Set<Integer> slotNumbers = squadList.stream()
				.flatMap(squad -> squad.getSlotList().stream().map(Slot::getNumber))
				.collect(Collectors.toUnmodifiableSet());
		int slotNumber = 1;
		while (slotNumbers.contains(slotNumber)) {
			slotNumber++;
		}
		return slotNumber;
	}

	/**
	 * Checks whether a channel is assigned to the event
	 *
	 * @return true if a channel has been assigned to the event
	 */
	public boolean isAssigned() {
		return !CollectionUtils.isEmpty(getDiscordInformation());
	}

	/**
	 * Returns the matching {@link EventDiscordInformation} for the given guild
	 *
	 * @param guildId to find discord information for
	 * @return optional information
	 */
	public Optional<EventDiscordInformation> getDiscordInformation(long guildId) {
		return getDiscordInformation().stream()
				.filter(eventDiscordInformation -> eventDiscordInformation.getGuild().getId() == guildId).findAny();
	}

	public boolean canRevokeShareable() {
		return getDiscordInformation().stream().allMatch(information -> information.getGuild().equals(getOwnerGuild()));
	}

	/**
	 * Shortcut for retrieving the {@link Locale} for the {@link #ownerGuild} of this event
	 */
	public Locale getOwnerGuildLocale() {
		return getOwnerGuild().getLocale();
	}

	public Set<Long> getRequirementsIds() {
		return requirements.stream().map(Requirement::getId).collect(Collectors.toUnmodifiableSet());
	}

	// Validator

	/**
	 * Checks whether the event has a slot with a non-unique slot number
	 *
	 * @return true if a duplicated slot number has been found
	 */
	private boolean hasDuplicatedSlotNumber() {
		HashSet<Integer> slotNumbers = new HashSet<>();
		for (Squad squad : getSquadList()) {
			for (Slot slot : squad.getSlotList()) {
				if (!slotNumbers.add(slot.getNumber())) {
					log.debug("Duplicated Slot number found: {}: {}", slot.getNumber(), slot.getName());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Validates the event. If the validation fails, an exception is thrown
	 *
	 * @throws BusinessRuntimeException If the validation failed
	 */
	public void validate() {
		if (hasDuplicatedSlotNumber()) {
			throw BusinessRuntimeException.builder().title("Slotnummern müssen innerhalb eines Events eindeutig sein.").build();
		}

		if (getDetails().size() > 23) { //Discord only allows 25 fields. Time plan, mission type and reserveParticipating each block one field
			throw BusinessRuntimeException.builder().title("Es dürfen nur 23 Detailfelder angegeben werden.").build();
		}
	}

	// Setter

	/**
	 * Set parents in child objects
	 */
	public void setBackReferences() {
		for (Squad squad : getSquadList()) {
			squad.setEvent(this);
			for (Slot slot : squad.getSlotList()) {
				slot.setSquad(squad);
			}
		}

		for (EventField field : getDetails()) {
			field.setEvent(this);
		}

		if (getDiscordInformation() != null) {
			getDiscordInformation().forEach(information -> information.setEvent(this));
		}
	}

	public void removeReservedForDefaultGuild() {
		getSquadList().forEach(squad -> {
			removeDefaultGuild(squad.getReservedFor(), squad::setReservedFor);
			squad.getSlotList().forEach(slot ->
					removeDefaultGuild(slot.getReservedFor(), slot::setReservedFor));
		});
	}

	private void removeDefaultGuild(Guild reservedFor, Consumer<Guild> consumer) {
		if (reservedFor != null && reservedFor.getId() == GUILD_PLACEHOLDER) {
			consumer.accept(null);
		}
	}

	/**
	 * Removes all {@link Squad#isEmpty() empty} squads
	 */
	public void removeEmptySquads() {
		getSquadList().removeIf(Squad::isEmpty);
	}

	/**
	 * Removes the given squad from the event
	 *
	 * @param squad to remove
	 */
	void removeSquad(Squad squad) {
		getSquadList().remove(squad);
	}

	public void unslotIfAlreadySlotted(User user) {
		findSlotOfUser(user).ifPresent(oldSlot -> oldSlot.unslotWithoutUpdate(user));
	}

	/**
	 * Informs the event about a slot update (slot, unslot, new slot(s) created, slot(s) removed).
	 * Uses {@link Event#moveReservists()} to change reserve if needed
	 */
	void slotUpdate() {
		moveReservists();
	}

	public void slotUpdateWithValidation() {
		validate();
		slotUpdate();
	}

	/**
	 * Moves reservists to empty slots and closes gaps in the Reserve-Squad
	 */
	private void moveReservists() {
		findSquadByName(RESERVE_NAME).ifPresent(reserve -> {
			if (!isFull()) {
				final List<Slot> reserveSlots = reserve.getSlotList().stream()
						.filter(Slot::isNotEmpty)
						.collect(Collectors.toCollection(ArrayList::new));
				getSquadList().stream()
						//Empty slots
						.flatMap(squad -> squad.getSlotList().stream().filter(Slot::isEmpty))
						.forEach(emptySlot -> reserveSlots.stream()
								//Only reserve that is allowed on the empty slot
								//Banned users can't be part of the reserve, check therefore not needed here
								.filter(reserveSlot -> emptySlot.getSlottable(reserveSlot.getUser()).state().isSlottingAllowed())
								.findFirst()
								.ifPresent(reserveSlot -> {
									final User reserveSlotUser = reserveSlot.getUser();
									reserveSlot.unslotWithoutUpdate(reserveSlotUser);
									emptySlot.slotWithoutUpdate(reserveSlotUser);
									reserveSlots.remove(reserveSlot); //Only set the remaining reserve on empty slots
								}));
			}

			List<Slot> reserveSlots = reserve.getSlotList();
			List<User> reserveUsers = reserveSlots.stream().filter(Slot::isNotEmpty).map(Slot::getUser).toList();

			//Empty reserve
			reserveSlots.stream().filter(Slot::isNotEmpty).forEach(slot -> slot.unslotWithoutUpdate(slot.getUser()));
			//Fill reserve from the beginning to close gaps
			for (int i = 0; i < reserveUsers.size(); i++) {
				reserveSlots.get(i).slotWithoutUpdate(reserveUsers.get(i));
			}
		});

		changeReserveIfNeeded();
	}

	/**
	 * Adds, removes or resizes the Reserve-Squad
	 */
	private void changeReserveIfNeeded() {
		Optional<Squad> reserve = findSquadByName(RESERVE_NAME);
		if (reserve.isEmpty()) {
			//Add reserve if the event is full, squads exist and the reserve is not yet present
			if (isFull() && !CollectionUtils.isEmpty(getSquadList())) {
				addReserve();
			}
		} else {
			Squad reserveSquad = reserve.get();
			if (!isFull()) {
				//Remove reserve if event is no longer full
				removeReserve(reserveSquad);
			} else if (getDesiredReserveSize() != reserveSquad.getSlotList().size()) {
				adjustReserveSize(reserveSquad);
			}
		}
	}

	/**
	 * Adds the Reserve-Squad to the event. It includes 25% of the event slots
	 */
	private void addReserve() {
		Squad reserveSquad = Squad.builder()
				.name(RESERVE_NAME)
				.slotList(new ArrayList<>())
				.event(this)
				.build();

		//Add 25% Slots of slotCount to reserve. At least 1 Slot
		List<Slot> reserveSlots = reserveSquad.getSlotList();
		for (int i = 0; i < getDesiredReserveSize(); i++) {
			int slotNumber = 100 + i;
			while (findSlot(slotNumber).isPresent()
					|| EventUtils.slotNumberPresent(reserveSlots, slotNumber)) {
				slotNumber++;
			}
			reserveSlots.add(Slot.builder()
					.number(slotNumber)
					.name("Reserve " + (i + 1))
					.squad(reserveSquad)
					.build());
		}

		getSquadList().add(reserveSquad);
	}

	/**
	 * Changes the reserve size to match 25% of the event slots. Will not shrink below count of reservists
	 *
	 * @param reserve reserve Squad
	 */
	private void adjustReserveSize(@NonNull Squad reserve) {
		List<Slot> oldSlotList = reserve.getSlotList();
		List<User> reserveUsers = oldSlotList.stream()
				.map(Slot::getUser).filter(Objects::nonNull)
				.toList();

		//Reduce the reserve size so that all persons already slotted remain so
		int newReserveSize = Math.max(getDesiredReserveSize(), reserveUsers.size());
		List<Slot> newReserveSlots = new ArrayList<>();
		for (int i = 0; i < newReserveSize; i++) {
			int slotNumber = 100 + i;
			while ((findSlot(slotNumber).isPresent() && !findSlot(slotNumber).get().isInReserve())
					|| EventUtils.slotNumberPresent(newReserveSlots, slotNumber)) {
				slotNumber++;
			}
			Slot slot = Slot.builder()
					.number(slotNumber)
					.name("Reserve " + (i + 1))
					.squad(reserve)
					.build();
			if (i < reserveUsers.size()) {
				slot.setUser(reserveUsers.get(i));
			}
			newReserveSlots.add(slot);
		}
		oldSlotList.clear();
		oldSlotList.addAll(newReserveSlots); //Here, slotList must not be reset (reserve.setSlotList(newReserveSlots);). Since the original list must be preserved (https://stackoverflow.com/questions/5587482/hibernate-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenc).
	}

	/**
	 * Removes the given Reserve-Squad from the event
	 *
	 * @param reserve Reserve-Squad
	 * @throws RuntimeException if the given reserve has any slotted user
	 */
	private void removeReserve(Squad reserve) {
		if (reserve.getSlotList().stream().anyMatch(Slot::isNotEmpty)) {
			log.error("Tried to delete non empty reserve in event {}", getId());
			throw new IllegalArgumentException("Reserve is not empty. Can't delete");
		}
		removeSquad(reserve);
	}

	/**
	 * Archives the event for the given guild. This removes the discord information for the guild and
	 * removes all notifications for the event, if the guild is the owner guild.
	 * <p>
	 * Don't forget to {@link EventArchiveEvent inform other systems} about the archiving process.
	 *
	 * @param guildId to archive event for
	 */
	public void archive(long guildId) {
		getDiscordInformation().removeIf(information -> information.getGuild().getId() == guildId);
		if (getOwnerGuild().getId() == guildId) {
			EventNotificationService.removeNotifications(getId());
		}
	}

	/**
	 * Removes the given requirement list from the list of requirements
	 *
	 * @param requirementList to remove
	 */
	public void removeRequirementList(RequirementList requirementList) {
		requirements.removeIf(requirement -> requirement.getRequirementList().equals(requirementList));
	}
}
