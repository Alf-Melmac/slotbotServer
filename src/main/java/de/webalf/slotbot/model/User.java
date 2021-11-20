package de.webalf.slotbot.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.DateUtils.getLocalDateTimeComparator;

/**
 * @author Alf
 * @since 06.09.2020
 */
@EqualsAndHashCode
@Entity
@Table(name = "discord_user",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}), @UniqueConstraint(columnNames = {"user_steam_id"})},
		schema = "public")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User {
	@Id
	@Column(name = "id")
	//Workaround to ignore generated values
	private long id;

	@Column(name = "user_steam_id")
	private Long steamId64;

	@Column(name = "user_external_calendar", nullable = false)
	private boolean externalCalendarIntegrationActive = false;

	@OneToMany(mappedBy = "user")
	private Set<Slot> slots = new HashSet<>();

	public static final long DEFAULT_USER_ID = 11111;

	@Builder
	public User(long id, Long steamId64) {
		this.id = id;
		this.steamId64 = steamId64;
	}

	public boolean isDefaultUser() {
		return getId() == DEFAULT_USER_ID;
	}

	public Optional<LocalDateTime> getLastEventDateTime() {
		return getSlots().stream()
				.map(Slot::getSquad)
				.map(Squad::getEvent)
				.map(Event::getDateTime)
				.filter(dateTime -> dateTime.isBefore(LocalDateTime.now())).min(getLocalDateTimeComparator());
	}

	public long countParticipatedEvents() {
		return getSlots().stream().filter(slot -> slot.getEvent().getDateTime().isBefore(LocalDateTime.now())).count();
	}

	public List<Event> getSlottedEvents() {
		return getSlots().stream().map(Slot::getEvent).collect(Collectors.toUnmodifiableList());
	}
}
