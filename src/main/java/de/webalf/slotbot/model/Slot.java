package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "slot", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slot extends AbstractIdEntity {
	@Column(name = "slot_name", length = 100)
	@Size(max = 80)
	@NotBlank
	private String name;

	@Column(name = "slot_number")
	@NotEmpty
	private int number;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "squad_id")
	@JsonBackReference
	private Squad squad;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public Slot(long id, String name, int number, Squad squad, User user) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.squad = squad;
		this.user = user;
	}

	// Getter

	boolean isSlotWithNumber(int slotNumber) {
		return getNumber() == slotNumber;
	}

	boolean isSlotWithSlottedUser(User user) {
		return isNotEmpty() && getUser().equals(user);
	}

	boolean isEmpty() {
		return getUser() == null;
	}

	boolean isNotEmpty() {
		return !isEmpty();
	}

	public Event getEvent() {
		return getSquad().getEvent();
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
		if (user.equals(getUser())) {
			//TODO: Return a warning, not a exception
			throw BusinessRuntimeException.builder().title("Die Person ist bereits auf diesem Slot").build();
		} else if (isEmpty() || isSlotWithSlottedUser(user)) {
			//Remove the user from any other slot in the Event
			getEvent().findSlotOfUser(user).ifPresent(slot -> slot.unslotWithoutUpdate(user));
			setUser(user);
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
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

	/**
	 * Swaps the users of the current and the given slot
	 */
	public void swapUsers(@NonNull Slot slot) {
		User slotUser = getUser();
		setUser(slot.getUser());
		slot.setUser(slotUser);

		getEvent().slotUpdate();
	}
}
