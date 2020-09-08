package de.webalf.slotbot.model;

import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.persistence.converter.LocalDateTimePersistenceConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Entity
@Table(name = "log", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionLog extends AbstractIdEntity {
	@Column(name = "action")
	@Enumerated(EnumType.STRING)
	@NotBlank
	private LogAction action;

	@Column(name = "time")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@NonNull
	private LocalDateTime time;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@NonNull
	private User user;

	@Column(name = "action_object_id")
	private long actionObjectId;

	@Builder
	ActionLog(@NonNull LogAction action,
	          @NonNull User user,
	          long actionObjectId) {
		this.action = action;
		time = LocalDateTime.now();
		this.user = user;
		this.actionObjectId = actionObjectId;
	}
}
