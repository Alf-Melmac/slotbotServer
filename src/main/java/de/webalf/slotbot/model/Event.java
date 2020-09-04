package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.persistence.converter.LocalDateTimePersistenceConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "event", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Event extends AbstractIdEntity {
	@Column(name = "event_name", length = 100)
	@NotEmpty
	@Size(max = 80)
	private String name;

	@Column(name = "event_date")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime dateTime;

	@Column(name = "event_description", length = 4000)
	@Size(max = 3200)
	private String description;

	@Column(name = "event_channel")
	private long channel;

	@OneToMany(mappedBy = "event", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@JsonManagedReference
	private List<Squad> squadList;

	@Column(name = "event_info_msg")
	private long infoMsg;

	@Column(name = "event_slotlist_msg")
	private long slotListMsg;

	@Transient
	private int slotCount;

	@Builder
	public Event(final long id,
	             final String name,
	             final LocalDateTime dateTime,
	             final String description,
	             final long channel,
	             final List<Squad> squadList,
	             final long infoMsg,
	             final long slotListMsg) {
		this.id = id;
		this.name = name;
		this.dateTime = dateTime;
		this.description = description;
		this.channel = channel;
		this.squadList = squadList;
		this.infoMsg = infoMsg;
		this.slotListMsg = slotListMsg;

		assertUniqueSlotNumbers();
		updateSlotCount();
	}

	private static final String RESERVE_NAME = "Reserve";

	// Getter

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
	 * @param userId associated to the slot
	 * @return the slot or an empty Optional if slot with given user doesn't exist
	 */
	public Optional<Slot> findSlotOfUser(long userId) {
		for (Squad squad : getSquadList()) {
			Optional<Slot> slotOptional = squad.findSlotOfUser(userId);
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

	private boolean isFull() {
		for (Squad squad : getSquadList()) {
			for (Slot slot : squad.getSlotList()) {
				if (slot.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	private int getReserveSize() {
		return getSlotCount() < 4 ? 1 : getSlotCount() / 4;
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
					log.debug("Duplicated Slot number found: " + slot.getNumber() + slot.getName());
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
	public void assertUniqueSlotNumbers() {
		if (hasDuplicatedSlotNumber()) {
			throw BusinessRuntimeException.builder().title("Slotnummern müssen innerhalb eines Events eindeutig sein.").build();
		}
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

	private Event removeSquad(Squad squad) {
		getSquadList().remove(squad);
		return this;
	}

	/**
	 * Recounts the slotCount and adjusts reserve if needed
	 */
	public void updateSlotCount() {
		setSlotCount(0);
		for (Squad squad : getSquadList()) {
			setSlotCount(getSlotCount() + squad.getSlotList().size());
		}

		changeReserveIfNeeded();
	}

	/**
	 * Adds or removes the "Reserve"-Squad
	 *
	 * @return Optional of the event if reserve has been or removed. Empty Optional if nothing has changed
	 */
	public Optional<Event> changeReserveIfNeeded() {
		Optional<Squad> reserve = findSquadByName(RESERVE_NAME);
		if (reserve.isEmpty()) {
			//Add reserve if event is full and not already exists
			if (isFull()) {
				return Optional.of(addReserve());
			}
		} else if (!isFull()) {
			//Remove reserve if event is no longer full
			return Optional.of(removeReserve(reserve.get()));
		} else if (getReserveSize() != reserve.get().getSlotList().size()) {
			return Optional.of(adjustReserveSize());
		}
		return Optional.empty();
	}

	private Event adjustReserveSize() {
		findSquadByName(RESERVE_NAME).ifPresent(reserve -> {
			List<Slot> reserveSlots = reserve.getSlotList();
			List<Long> reserveUsers = reserveSlots.stream().map(Slot::getUserId).collect(Collectors.toList());
			final int reserveSizeToAchieve = getReserveSize();

			//Reduce the reserve size so that all persons already slotted remain so
			int newReserveSize = Math.max(reserveSizeToAchieve, reserveUsers.size());
			List<Slot> newReserveSlots = new ArrayList<>();
			for (int i = 0; i < newReserveSize; i++) {
				Slot slot = Slot.builder()
						.number(100 + i)
						.name("Reserve " + i)
						.squad(reserve)
						.build();
				if (i <= reserveUsers.size()) {
					slot.setUserId(reserveUsers.get(i));
				}
				newReserveSlots.add(slot);
			}
			reserve.setSlotList(newReserveSlots);
		});

		return this;
	}

	/**
	 * Adds the "Reserve"-Squad to the event. It includes 25% of the event slots
	 *
	 * @return Event the reserve has been added to
	 */
	private Event addReserve() {
		Squad reserveSquad = Squad.builder()
				.name(RESERVE_NAME)
				.slotList(new ArrayList<>())
				.event(this)
				.build();

		//Add 25% Slots of slotCount to reserve. At least 1 Slot
		List<Slot> reserveSlots = reserveSquad.getSlotList();
		for (int i = 0; i < getReserveSize(); i++) {
			reserveSlots.add(Slot.builder()
					.number(100 + i)
					.name("Reserve " + (i + 1))
					.squad(reserveSquad)
					.build());
		}

		getSquadList().add(reserveSquad);
		return this;
	}

	/**
	 * Removes the given Reserve Squad from the event
	 *
	 * @param reserve Reserve-Squad
	 * @return the event the reserve has been removed from
	 * @throws RuntimeException if the given reserve has any slotted user
	 */
	private Event removeReserve(Squad reserve) {
		if (reserve.getSlotList().stream().anyMatch(slot -> slot.getUserId() != 0)) {
			log.error("Tried to delete non empty reserve in event " + getName());
			throw new RuntimeException("Reserve is not empty. Can't delete");
		}
		return removeSquad(reserve);
	}

	/**
	 * Informs the event about an slot. Uses {@link Event#changeReserveIfNeeded()} to create reserve if needed
	 */
	public void slotPerformed() {
		changeReserveIfNeeded();
	}

	/**
	 * Informs the event about an unslot. Fills the empty slot with a reservist, if available
	 *
	 * @param slot that is now empty
	 */
	public void unslotPerformed(Slot slot) {
		//Add reservist to empty slot
		findSquadByName(RESERVE_NAME)
				.flatMap(reserve -> reserve.getSlotList().stream().filter(reserveSlot -> !reserveSlot.isEmpty()).findFirst())
				.ifPresent(reserveSlot -> {
					//TODO: Move the service workflow to the slot. switch caused by a slot change in a event action
					final long reserveSlotUser = reserveSlot.getUserId();
					slot.slot(reserveSlotUser);
					reserveSlot.unslot(reserveSlotUser);
				});

		changeReserveIfNeeded();
	}
}
