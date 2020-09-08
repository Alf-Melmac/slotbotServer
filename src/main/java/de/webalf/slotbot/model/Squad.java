package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "squad", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Squad extends AbstractIdEntity {
	@Column(name = "squad_name")
	@Size(max = 80)
	private String name;

	@OneToMany(mappedBy = "squad", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Slot> slotList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;

	@Builder
	public Squad(long id, String name, List<Slot> slotList, Event event) {
		this.id = id;
		this.name = name;
		this.slotList = slotList;
		this.event = event;
	}

	// Getter

	/**
	 * Finds a slot by its number
	 *
	 * @param slotNumber associated to the slot
	 * @return the slot or an empty Optional if slot with given number doesn't exist
	 */
	Optional<Slot> findSlot(int slotNumber) {
		for (Slot slot : getSlotList()) {
			if (slot.isSlotWithNumber(slotNumber)) {
				return Optional.of(slot);
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
	Optional<Slot> findSlotOfUser(User user) {
		for (Slot slot : getSlotList()) {
			if (slot.isSlotWithSlottedUser(user)) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}

	// Setter

	/**
	 * Adds a new slot to the squad
	 *
	 * @param newSlot to add
	 */
	public void addSlot(Slot newSlot) {
		newSlot.setSquad(this);
		slotList.add(newSlot);
		getEvent().updateSlotCount();
	}

	/**
	 * Removes given Slot from the squad
	 *
	 * @param slot to delete
	 */
	public void deleteSlot(Slot slot) {
		getSlotList().remove(slot);
		getEvent().updateSlotCount();
	}
}
