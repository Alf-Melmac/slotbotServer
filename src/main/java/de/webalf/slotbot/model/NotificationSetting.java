package de.webalf.slotbot.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Entity
@Table(name = "notification_setting", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class NotificationSetting extends AbstractSuperIdEntity {
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@NonNull
	private User user;

	@Column(name = "hours_before")
	private int hoursBeforeEvent;

	@Column(name = "minutes_before")
	private int minutesBeforeEvent;

	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;
}
