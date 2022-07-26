package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.service.bot.EventNotificationService;
import de.webalf.slotbot.util.EventUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.model.Squad.RESERVE_NAME;
import static de.webalf.slotbot.util.MaxLength.*;

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

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "event_type")
	@NotNull
	private EventType eventType;

	@Column(name = "event_description", length = EMBEDDABLE_DESCRIPTION_DB)
	@Size(max = EMBEDDABLE_DESCRIPTION)
	private String description;

	@Column(name = "event_mission_type", length = TEXT_DB)
	@Size(max = TEXT)
	private String missionType;

	@Column(name = "event_mission_length", length = TEXT_DB)
	@Size(max = TEXT)
	private String missionLength;

	@Column(name = "event_picture_url", length = URL_DB)
	@Size(max = URL)
	private String pictureUrl;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<EventField> details;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<Squad> squadList;

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
	 * Finds a slot by its position
	 *
	 * @param squadPosition position to get squad for
	 * @return the squad
	 * @throws BusinessRuntimeException if there is no squad at the given position
	 */
	public Squad findSquadByPosition(int squadPosition) {
		final List<Squad> squad = getSquadList();
		if (squad.size() <= squadPosition || squadPosition < 0) {
			throw BusinessRuntimeException.builder().title("Den Squad konnte ich nicht finden.").build();
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
	private Set<Squad> getSquadsExceptReserve() {
		return getSquadList().stream().filter(squad -> !squad.isReserve()).collect(Collectors.toUnmodifiableSet());
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

	/**
	 * Returns the matching {@link EventDiscordInformation} for the given guild
	 *
	 * @param guild to find discord information for
	 * @return optional information
	 */
	public Optional<EventDiscordInformation> getDiscordInformation(@NonNull Guild guild) {
		return getDiscordInformation(guild.getId());
	}

	public boolean canRevokeShareable() {
		return getDiscordInformation().stream().allMatch(information -> information.getGuild().equals(getOwnerGuild()));
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
					log.debug("Duplicated Slot number found: " + slot.getNumber() + ": " + slot.getName());
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
	 * Sets the date part of the event DateTime
	 *
	 * @param date to set
	 */
	public void setDate(@NonNull LocalDate date) {
		setDateTime(getDateTime().withYear(date.getYear()).withMonth(date.getMonth().getValue()).withDayOfMonth(date.getDayOfMonth()));
	}

	/**
	 * Sets the time part of the event DateTime
	 *
	 * @param time to set
	 */
	public void setTime(@NonNull LocalTime time) {
		setDateTime(getDateTime().withHour(time.getHour()).withMinute(time.getMinute()));
	}

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
				//Fills empty slots with reservists
				List<Slot> emptySlots = getSquadList().stream().flatMap(squad -> squad.getSlotList().stream().filter(Slot::isEmpty)).collect(Collectors.toList());
				emptySlots.forEach(slot -> reserve.getSlotList().stream()
						.filter(reserveSlot -> !reserveSlot.isEmpty()).findFirst()
						.ifPresent(reserveSlot -> {
							User reserveSlotUser = reserveSlot.getUser();
							reserveSlot.unslotWithoutUpdate(reserveSlotUser);
							slot.slotWithoutUpdate(reserveSlotUser);
						}));
			}

			List<Slot> reserveSlots = reserve.getSlotList();
			List<User> reserveUsers = reserveSlots.stream().filter(reserveSlot -> !reserveSlot.isEmpty()).map(Slot::getUser).collect(Collectors.toUnmodifiableList());

			//Empty reserve
			reserveSlots.forEach(slot -> slot.unslotWithoutUpdate(slot.getUser()));
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
			if (isFull() && !ListUtils.isEmpty(getSquadList())) {
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
				.collect(Collectors.toList());

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
			log.error("Tried to delete non empty reserve in event " + getName());
			throw new IllegalArgumentException("Reserve is not empty. Can't delete");
		}
		removeSquad(reserve);
	}

	private static final Random RANDOM = new Random();

	/**
	 * Returns a random empty slot from the event.
	 *
	 * @param user to find slot for
	 * @return random empty slot
	 */
	public Slot randomSlot(User user) {
		final List<Slot> emptySlots = getSquadList().stream()
				.filter(Squad::hasEmptySlot)
				.flatMap(squad -> squad.getSlotList().stream().filter(slot -> slot.slotIsPossible(user)))
				.collect(Collectors.toUnmodifiableList());
		if (CollectionUtils.isEmpty(emptySlots)) {
			throw BusinessRuntimeException.builder().title("Kein freier Slot vorhanden.").build();
		}
		return emptySlots.get(RANDOM.nextInt(emptySlots.size()));
	}

	public void archive(long guildId) {
		if (LocalDateTime.now().isBefore(getDateTime())) {
			throw BusinessRuntimeException.builder().title("Es können nur Events in der Vergangenheit archiviert werden.").build();
		}

		getDiscordInformation(guildId).ifPresent(informationOfGuild -> getDiscordInformation().remove(informationOfGuild));
		if (getOwnerGuild().getId() == guildId) {
			EventNotificationService.removeNotifications(getId());
		}
	}
}
