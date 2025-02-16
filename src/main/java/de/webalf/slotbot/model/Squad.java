package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT_DB;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "squad", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Squad extends AbstractSuperIdEntity {
	@Column(name = "squad_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@OneToMany(mappedBy = "squad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT) //FetchMode adds n+1 but prevents duplicates in list
	@OrderBy("number")
	@JsonManagedReference
	private List<Slot> slotList;

	@ManyToOne
	@JoinColumn(name = "squad_reserved_for")
	private Guild reservedFor;

	@ManyToMany
	@JoinTable(name = "squad_requirement",
			joinColumns = @JoinColumn(name = "squad_id", foreignKey = @ForeignKey(name = "squad_fk")),
			inverseJoinColumns = @JoinColumn(name = "requirement_id", foreignKey = @ForeignKey(name = "requirement_fk")))
	private Set<Requirement> requirements;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;

	public static final String RESERVE_NAME = "Reserve";

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

	public boolean hasEmptySlot() {
		return getSlotList().stream().anyMatch(Slot::isEmpty);
	}

	boolean isEmpty() {
		return getSlotList().isEmpty();
	}

	public Set<Long> getRequirementsIds() {
		return requirements.stream().map(Requirement::getId).collect(Collectors.toUnmodifiableSet());
	}

	// Setter

	/**
	 * Adds a new slot to the squad
	 *
	 * @param newSlot to add
	 */
	public void addSlot(Slot newSlot) {
		if (isReserve()) {
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
		if (isEmpty()) {
			getEvent().removeSquad(this);
		}

		getEvent().slotUpdate();
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
