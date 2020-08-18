package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

	/**
	 * Checks whether the event has a slot with a non-unique slot number
	 *
	 * @return true if a duplicated slot number has been found
	 */
	public boolean hasDuplicatedSlotNumber() {
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
}
