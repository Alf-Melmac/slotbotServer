package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.DurationPersistenceConverter;
import de.webalf.slotbot.model.enums.LogAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Entity
@Table(name = "log", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ActionLog extends AbstractSuperIdEntity {
	@Column(name = "action")
	@Enumerated(EnumType.STRING)
	@NonNull
	private LogAction action;

	@Column(name = "time_gap")
	@Convert(converter = DurationPersistenceConverter.class)
	private Duration timeGap;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@NonNull
	private User user;

	@Column(name = "action_object_id")
	private long actionObjectId;

	public String getTimeGapString() {
		long sec = getTimeGap().getSeconds();
		return "%02d:%02d".formatted(sec / 3600, (sec % 3600) / 60);
	}
}
