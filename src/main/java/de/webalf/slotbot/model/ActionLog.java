package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.DurationPersistenceConverter;
import de.webalf.slotbot.model.enums.LogAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Entity
@Table(name = "log", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ActionLog extends AbstractIdEntity {
	@Column(name = "action")
	@Enumerated(EnumType.STRING)
	@NonNull
	private LogAction action;

	@Column(name = "time_gap")
	@Convert(converter = DurationPersistenceConverter.class)
	@NonNull
	private Duration timeGap;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@NonNull
	private User user;

	@Column(name = "action_object_id")
	private long actionObjectId;

	@Builder
	ActionLog(@NonNull LogAction action,
	          @NonNull Duration timeGap,
	          @NonNull User user,
	          long actionObjectId) {
		this.action = action;
		this.timeGap = timeGap;
		this.user = user;
		this.actionObjectId = actionObjectId;
	}

	public String getTimeGapString() {
		long sec = getTimeGap().getSeconds();
		return String.format("%02d:%02d", sec/3600, (sec%3600)/60);
	}
}
