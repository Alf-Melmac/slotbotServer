package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
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

	@JoinColumn(name = "user_id")
	private long userId;

	@Builder
	public Slot(final long id, final String name, final int number, final Squad squad, final long userId) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.squad = squad;
		this.userId = userId;
	}

	// Getter

	public boolean isSlotWithNumber(int slotNumber) {
		return getNumber() == slotNumber;
	}

	public boolean isSlotWithSlottedUser(long userId) {
		return getUserId() == userId;
	}

	public boolean isEmpty() {
		return getUserId() == 0;
	}

	// Setter

	public void setUserIdString(String userIdString) {
		setUserId(Long.parseLong(userIdString));
	}

	/**
	 * Adds the given user from the slot if no other user occupies the slot
	 *
	 * @param userId user to slot
	 */
	public void slot(long userId) {
		if (isEmpty()) {
			setUserId(userId);
			getSquad().getEvent().slotPerformed();
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		}
	}

	/**
	 * Removes the given user from the slot if no other user occupies the slot
	 *
	 * @param userId user to unslot
	 */
	public void unslot(long userId) {
		if (isSlotWithSlottedUser(userId) || isEmpty()) {
			setUserId(0);
			getSquad().getEvent().unslotPerformed(this);
		} else {
			throw BusinessRuntimeException.builder().title("Auf dem Slot befindet sich eine andere Person").build();
		}
	}
}
