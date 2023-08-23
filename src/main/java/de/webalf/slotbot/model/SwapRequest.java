package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 22.08.2023
 */
@Entity
@Table(name = "swap_request", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"swap_request_requester", "swap_request_foreign"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class SwapRequest extends AbstractSuperIdEntity {
	@ManyToOne(targetEntity = Slot.class)
	@JoinColumn(name = "swap_request_requester", nullable = false)
	private Slot requesterSlot;

	@ManyToOne(targetEntity = Slot.class)
	@JoinColumn(name = "swap_request_foreign", nullable = false)
	private Slot foreignSlot;

	@Column(name = "swap_request_message_id")
	private long messageId;
}
