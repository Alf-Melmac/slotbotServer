package de.webalf.slotbot.feature.notifications.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.util.DateUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
	private Event event; //Not yet implemented

	/**
	 * Calculates the time in minutes from now to the notification time
	 *
	 * @param eventTime event start time
	 * @return delay until notification must be sent
	 */
	public int getNotificationDelay(LocalDateTime eventTime) {
		return (int) ChronoUnit.MINUTES.between(DateUtils.now(), getNotificationTime(eventTime));
	}

	private LocalDateTime getNotificationTime(@NonNull LocalDateTime eventTime) {
		return eventTime.minusHours(hoursBeforeEvent).minusMinutes(minutesBeforeEvent);
	}
}
