package de.webalf.slotbot.feature.action_log.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import de.webalf.slotbot.model.User;
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
	private Duration timeGap;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "log_user_fk"))
	@NonNull
	private User user;

	@Column(name = "action_object_id")
	private long actionObjectId;
}
