package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.ShortEventInformationDto;
import de.webalf.slotbot.util.BooleanUtils;
import de.webalf.slotbot.util.EventUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.thymeleaf.util.ListUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.webalf.slotbot.model.Squad.RESERVE_NAME;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "event", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "event_channel"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Event extends AbstractIdEntity {
	@Column(name = "event_name", length = 100)
	@NotBlank
	@Size(max = 80)
	private String name;

	@Column(name = "event_date")
	@NotNull
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime dateTime;

	@Column(name = "event_creator")
	@NotBlank
	@Size(max = 80)
	private String creator;

	@Column(name = "event_hidden")
	private boolean hidden;

	@Column(name = "event_channel")
	private Long channel;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<Squad> squadList;

	@Column(name = "event_info_msg")
	private Long infoMsg;

	@Column(name = "event_slotlist_msg")
	private Long slotListMsg;

	@Column(name = "event_description", length = 2560)
	@Size(max = MessageEmbed.TEXT_MAX_LENGTH)
	private String description;

	@Column(name = "event_picture_url", length = 2083)
	@Size(max = 1666)
	private String pictureUrl;

	@Column(name = "event_mission_type", length = 100)
	@Size(max = 80)
	private String missionType;

	@Column(name = "event_respawn")
	private Boolean respawn;

	@Column(name = "event_mission_length", length = 100)
	@Size(max = 80)
	private String missionLength;

	@Column(name = "event_reserve_participating")
	private Boolean reserveParticipating;

	@Column(name = "event_mod_pack", length = 100)
	@Size(max = 80)
	private String modPack;

	@Column(name = "event_map", length = 100)
	@Size(max = 80)
	private String map;

	@Column(name = "event_mission_time", length = 100)
	@Size(max = 80)
	private String missionTime;

	@Column(name = "event_navigation", length = 100)
	@Size(max = 80)
	private String navigation;

	@Column(name = "event_technical_teleport", length = 100)
	@Size(max = 80)
	private String technicalTeleport;

	@Column(name = "event_medical_system", length = 100)
	@Size(max = 80)
	private String medicalSystem;

	@Builder
	public Event(long id,
	             String name,
	             LocalDateTime dateTime,
	             String creator,
	             Boolean hidden,
	             Long channel,
	             List<Squad> squadList,
	             Long infoMsg,
	             Long slotListMsg,
	             String description,
	             String pictureUrl,
	             String missionType,
	             Boolean respawn,
	             String missionLength,
	             Boolean reserveParticipating,
	             String modPack,
	             String map,
	             String missionTime,
	             String navigation,
	             String technicalTeleport,
	             String medicalSystem) {
		this.id = id;
		this.name = name;
		this.dateTime = dateTime;
		this.creator = creator;
		this.hidden = BooleanUtils.falseIfNull(hidden);
		this.channel = channel;
		this.squadList = squadList;
		this.infoMsg = infoMsg;
		this.slotListMsg = slotListMsg;

		this.description = description;
		this.pictureUrl = pictureUrl;
		this.missionType = missionType;
		this.respawn = respawn;
		this.missionLength = missionLength;
		this.reserveParticipating = reserveParticipating;
		this.modPack = modPack;
		this.map = map;
		this.missionTime = missionTime;
		this.navigation = navigation;
		this.technicalTeleport = technicalTeleport;
		this.medicalSystem = medicalSystem;

		validation();
	}

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

	/**
	 * Checks if the event has already been printed
	 *
	 * @return true if event has already been printed
	 */
	public boolean isPrinted() {
		return getInfoMsg() != null || getSlotListMsg() != null;
	}

	private Optional<Squad> findSquadByName(String name) {
		for (Squad squad : getSquadList()) {
			if (squad.getName().equalsIgnoreCase(name)) {
				return Optional.of(squad);
			}
		}
		return Optional.empty();
	}

	private boolean isFull() {
		for (Squad squad : getSquadsExceptReserve()) {
			for (Slot slot : squad.getSlotList()) {
				if (slot.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns all usable squads. This excludes the reserve
	 *
	 * @return every squad that isn't the reserve
	 */
	private Set<Squad> getSquadsExceptReserve() {
		return getSquadList().stream().filter(squad -> !squad.isReserve()).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Returns 25% of the slot counts but at least one
	 *
	 * @return how big the reserve squad should be
	 */
	private int getDesiredReserveSize() {
		int slotCount = 0;
		for (Squad squad : getSquadsExceptReserve()) {
			slotCount += squad.getSlotList().size();
		}
		return slotCount < 4 ? 1 : (int) Math.ceil(slotCount / 4.);
	}

	/**
	 * Returns the most important information inside a {@link ShortEventInformationDto}
	 *
	 * @return important information
	 */
	public ShortEventInformationDto getShortInformation() {
		int emptySlots = 0;
		int slotCount = 0;
		int emptyReserveSlots = 0;

		for (Squad squad : getSquadList()) {
			for(Slot slot : squad.getSlotList()) {
				if (!slot.isInReserve()) {
					if (slot.isEmpty()) {
						emptySlots++;
					}
					slotCount++;
				} else if (slot.isEmpty()){
					emptyReserveSlots++;
				}
			}
		}

		return ShortEventInformationDto.builder()
				.emptySlotsCount(emptySlots)
				.slotCount(slotCount)
				.emptyReserveSlotsCount(emptyReserveSlots)
				.missionLength(getMissionLength())
				.build();
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
	 * Checks whether the event has a slot with a non-unique slot number
	 *
	 * @throws BusinessRuntimeException if a duplicated slot number has been found
	 */
	private void assertUniqueSlotNumbers() {
		if (hasDuplicatedSlotNumber()) {
			throw BusinessRuntimeException.builder().title("Slotnummern müssen innerhalb eines Events eindeutig sein.").build();
		}
	}

	/**
	 * Validates the event. If the validation fails, an exception is thrown
	 */
	private void validation() {
		assertUniqueSlotNumbers();
	}

	// Setter

	/**
	 * Sets the date part of the event DateTime
	 *
	 * @param date to set
	 */
	public void setDate(LocalDate date) {
		setDateTime(getDateTime().withDayOfMonth(date.getDayOfMonth()).withMonth(date.getMonth().getValue()).withYear(date.getYear()));
	}

	/**
	 * Sets the time part of the event DateTime
	 *
	 * @param time to set
	 */
	public void setTime(LocalTime time) {
		setDateTime(getDateTime().withHour(time.getHour()).withMinute(time.getMinute()));
	}

	public void setChannelString(String channelString) {
		setChannel(Long.parseLong(channelString));
	}

	public void setInfoMsgString(String infoMsgString) {
		setInfoMsg(Long.parseLong(infoMsgString));
	}

	public void setSlotListMsgString(String slotListMsgString) {
		setSlotListMsg(Long.parseLong(slotListMsgString));
	}

	void removeSquad(Squad squad) {
		getSquadList().remove(squad);
	}

	/**
	 * Informs the event about an slot update (slot, unslot, new slot(s) created, slot(s) removed).
	 * Uses {@link Event#moveReservists()} to change reserve if needed
	 */
	void slotUpdate() {
		moveReservists();
	}

	public void slotUpdateWithValidation() {
		validation();
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

			List<Slot> reserveSlots = reserve.getSlotListOrdered();
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
			while (findSlot(slotNumber).isPresent()) {
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
		List<User> reserveUsers = oldSlotList.stream().sorted(Comparator.comparing(Slot::getNumber))
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
}
