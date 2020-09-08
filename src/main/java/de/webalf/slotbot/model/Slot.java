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

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public Event getEvent() {
		return getSquad().getEvent();
	}

	// Setter

	/**
	 * Adds the given user from the slot if no other user occupies the slot
	 *
	 * @param user user to slot
	 */
	public void slot(User user) {
		if (isEmpty()) {
			setUser(user);
			getEvent().slotPerformed();
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		}
	}

	/**
	 * Removes the given user from the slot if no other user occupies the slot
	 *
	 * @param user user to unslot
	 */
	public void unslot(User user) {
		if (isSlotWithSlottedUser(user) || isEmpty()) {
			setUser(null);
			getEvent().unslotPerformed(this);
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		}
	}
}
