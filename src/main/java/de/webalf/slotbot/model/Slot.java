package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.TEXT;
import static de.webalf.slotbot.util.MaxLength.TEXT_DB;

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

	/**
	 * @return false if the squad of this slot is not known
	 */
	public boolean squadNullCheck() {
		if (getSquad() == null) {
			log.warn("Slot without squad: {}. Expected when adding a slot", getId());
			return false;
		}
		return true;
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

	boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean isBlocked() {
		return isNotEmpty() && getUser().isDefaultUser();
	}

	public Guild getEffectiveReservedFor() {
		return reservedFor != null ? reservedFor : getSquad().getReservedFor();
	}

	// Setter

	/**
	 * Adds the given user to the slot if no other user occupies the slot. If already slotted on another slot in the same event the slot will be changed
	 *
	 * @param user user to slot
	 */
	public void slot(User user) {
		slotWithoutUpdate(user);
		getEvent().slotUpdate();
	}

	/**
	 * @see Slot#slot(User)
	 * Doesn't trigger the slotUpdate
	 *
	 * @throws BusinessRuntimeException if the user is already slotted on this slot or the slot is already occupied
	 */
	void slotWithoutUpdate(@NonNull User user) {
		if (isSlotWithSlottedUser(user)) {
			throw BusinessRuntimeException.builder().title("Die Person ist bereits auf diesem Slot").build();
		} else if (!isEmpty()) {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		} else if (getEffectiveReservedFor() != null && user.getGuilds().stream().noneMatch(guild -> guild.equals(getEffectiveReservedFor()))) {
			throw BusinessRuntimeException.builder().title("Dieser Slot ist für Mitglieder einer anderen Gruppe reserviert").build();
		} else {
			//Remove the user from any other slot in the Event
			getEvent().findSlotOfUser(user).ifPresent(slot -> slot.unslotWithoutUpdate(user));
			setUser(user);
		}
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
	 * @see Slot#unslot(User)
	 * Doesn't trigger the slotUpdate
	 *
	 * @throws BusinessRuntimeException If the slot is occupied by a user other than the given user
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
}
