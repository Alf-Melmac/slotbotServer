package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.feature.slot_rules.Slottable;
import de.webalf.slotbot.util.SlotUtils;
import de.webalf.slotbot.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.webalf.slotbot.model.enums.SlottableState.*;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT_DB;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "slot", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Slf4j
public class Slot extends AbstractSuperIdEntity {
	@Column(name = "slot_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@Column(name = "slot_number")
	private int number;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "squad_id")
	@JsonBackReference
	private Squad squad;

	@ManyToOne(targetEntity = Guild.class)
	@JoinColumn(name = "slot_reserved_for")
	private Guild reservedFor;

	@ManyToMany
	@JoinTable(name = "slot_requirement",
			joinColumns = @JoinColumn(name = "slot_id", foreignKey = @ForeignKey(name = "slot_fk")),
			inverseJoinColumns = @JoinColumn(name = "requirement_id", foreignKey = @ForeignKey(name = "requirement_fk")))
	@Builder.Default
	private Set<Requirement> requirements = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "slot_replacement", length = TEXT_DB)
	@Size(max = TEXT)
	private String replacementText;

	// Getter

	public Event getEvent() {
		return getSquad().getEvent();
	}

	public boolean isInReserve() {
		return getSquad().isReserve();
	}

	boolean isSlotWithNumber(int slotNumber) {
		return getNumber() == slotNumber;
	}

	boolean isSlotWithSlottedUser(User user) {
		return isNotEmpty() && getUser().equals(user);
	}

	public boolean isEmpty() {
		return getUser() == null;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean isBlocked() {
		return isNotEmpty() && getUser().isDefaultUser();
	}

	public Guild getEffectiveReservedFor() {
		return reservedFor != null ? reservedFor : getSquad().getReservedFor();
	}

	/**
	 * @see SlotUtils#getEffectiveReservedForDisplay(Guild, Squad)
	 */
	public Guild getEffectiveReservedForDisplay() {
		return SlotUtils.getEffectiveReservedForDisplay(getReservedFor(), getSquad());
	}

	public Set<Requirement> getEffectiveRequirements() {
		return Stream.of(getEvent().getRequirements(), getSquad().getRequirements(), getRequirements())
				.flatMap(Collection::stream)
				.collect(Collectors.toUnmodifiableSet());
	}

	public Set<Long> getRequirementsIds() {
		return requirements.stream().map(Requirement::getId).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Returns the replacement text for this slot if it is blocked
	 *
	 * @return replacement text or null if not blocked
	 */
	public String getReplacementTextOrDefault() {
		if (!isBlocked()) {
			return null;
		}
		return StringUtils.isEmpty(replacementText) ? "Gesperrt" : replacementText;
	}

	/**
	 * Determines the usability of the slot for the given user
	 *
	 * @param user to be slotted
	 * @return info about usability
	 */
	public Slottable getSlottable(@NonNull User user) {
		if (isNotEmpty()) {
			if (isBlocked()) {
				return new Slottable(NO_BLOCKED);
			}
			if (isSlotWithSlottedUser(user)) {
				return new Slottable(YES_OWN);
			}
			return new Slottable(NO);
		}

		if (!user.partOfGuild(getEffectiveReservedFor())) {
			return new Slottable(NO_RESERVED);
		}

		final Set<Requirement> notFulfilledRequirements = user.getNotFulfilledRequirements(getEffectiveRequirements());
		if (!notFulfilledRequirements.isEmpty()) {
			final boolean includesEnforced = notFulfilledRequirements.stream().map(Requirement::getRequirementList).anyMatch(RequirementList::isEnforced);
			return new Slottable(includesEnforced ? NO_REQUIREMENTS_NOT_MET : YES_REQUIREMENTS_NOT_MET, notFulfilledRequirements);
		}

		return new Slottable(YES);
	}

	// Setter

	/**
	 * Adds the given user to the slot. Requires any prerequisites to be already checked.
	 *
	 * @param user user to slot
	 */
	public void slot(@NonNull User user) {
		slotWithoutUpdate(user);
		getEvent().slotUpdate();
	}

	/**
	 * Doesn't trigger the slotUpdate
	 *
	 * @see #slot(User)
	 */
	void slotWithoutUpdate(@NonNull User user) {
		setUser(user);
	}

	/**
	 * Removes the given user from the slot if no other user occupies the slot
	 *
	 * @param user user to unslot
	 */
	public void unslot(@NonNull User user) {
		unslotWithoutUpdate(user);
		getEvent().slotUpdate();
	}

	/**
	 * Doesn't trigger the slotUpdate
	 *
	 * @throws BusinessRuntimeException If the slot is occupied by a user other than the given user
	 * @see Slot#unslot(User)
	 */
	void unslotWithoutUpdate(User user) {
		if (isSlotWithSlottedUser(user) || isEmpty()) {
			setUser(null);
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		}
	}

	public void blockSlot(@NonNull User defaultUser, @NotBlank String replacementName) {
		if (isNotEmpty() && !isBlocked()) {
			throw BusinessRuntimeException.builder().title("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.").build();
		} else if (getSquad().isReserve()) {
			throw BusinessRuntimeException.builder().title("In der Reserve kann kein Slot blockiert werden.").build();
		}
		setUser(defaultUser);
		setReplacementText(replacementName);

		getEvent().slotUpdate();
	}

	/**
	 * Swaps the users of the current and the given slot
	 */
	public void swapUsers(@NonNull Slot slot) {
		User slotUser = getUser();
		setUser(slot.getUser());
		slot.setUser(slotUser);
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
