package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.exception.ForbiddenException;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	@NotBlank
	@Size(max = 80)
	private String name;

	@OneToMany(mappedBy = "squad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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

	static final String RESERVE_NAME = "Reserve";

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

	/**
	 * Validates if the name equals {@link Squad#RESERVE_NAME}
	 *
	 * @return true if the name matches
	 */
	public boolean isReserve() {
		return getName().equals(RESERVE_NAME);
	}

	@Deprecated //@OrderBy is now used. Check if ordered
	public List<Slot> getSlotListOrdered() {
		return getSlotList().stream().sorted(Comparator.comparing(Slot::getNumber)).collect(Collectors.toUnmodifiableList());
	}

	public boolean hasEmptySlot() {
		return getSlotList().stream().anyMatch(Slot::isEmpty);
	}

	// Setter

	/**
	 * Adds a new slot to the squad
	 *
	 * @param newSlot to add
	 */
	public void addSlot(Slot newSlot) {
		if (newSlot.squadNullCheck() && isReserve()) {
			throw new ForbiddenException("Zur Reserve dürfen keine Slots hinzugefügt werden.");
		}

		newSlot.setSquad(this);
		slotList.add(newSlot);
		getEvent().slotUpdateWithValidation();
	}

	/**
	 * Removes given slot from the squad
	 *
	 * @param slot to delete
	 */
	public void deleteSlot(Slot slot) {
		if (isReserve()) {
			throw new ForbiddenException("Ein Slot der Reserve kann nicht gelöscht werden.");
		}
		if (slot.isNotEmpty() && !slot.getUser().isDefaultUser()) {
			throw new ForbiddenException("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.");
		}

		getSlotList().remove(slot);
		deleteSquadIfEmpty();

		getEvent().slotUpdate();
	}

	/**
	 * Deletes the given squad, if no person is slotted on any slot
	 *
	 * @return true if Squad was deleted
	 */
	public boolean deleteSquadIfEmpty() {
		if (getSlotList().isEmpty()) {
			getEvent().removeSquad(this);
			return true;
		}
		return false;
	}
}
