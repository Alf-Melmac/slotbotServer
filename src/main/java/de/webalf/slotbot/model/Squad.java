package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public class Squad extends AbstractIdEntity {
	@Column(name = "squad_name")
	@Size(max = 80)
	private String name;

	@OneToMany(mappedBy = "squad", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@JsonManagedReference
	private List<Slot> slotList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;

	@Builder
	public Squad(final long id, final String name, final List<Slot> slotList, Event event) {
		this.id = id;
		this.name = name;
		this.slotList = slotList;
		this.event = event;
	}

	public Optional<Slot> findSlot(int slotNumber) {
		for (Slot slot : getSlotList()) {
			if (slot.isSlotWithNumber(slotNumber)) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}

	public Optional<Slot> findSlotOfUser(long userId) {
		for (Slot slot : getSlotList()) {
			if (slot.isSlotWithSlottedUser(userId)) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}
}
