package de.webalf.slotbot.model;

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
@Table(name = "slot")
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

	public boolean isSlotWithNumber(int slotNumber) {
		return getNumber() == slotNumber;
	}

	public boolean isSlotWithSlottedUser(long userId) {
		return getUserId() == userId;
	}

	public void slot(long userId) {
		setUserId(userId);
	}

	public void unslot(long userId) {
		if (getUserId() == userId || getUserId() == 0) {
			setUserId(0);
		} else {
			//TODO BRE
			throw new RuntimeException("Auf dem Slot befindet sich eine andere Person");
		}
	}
}
