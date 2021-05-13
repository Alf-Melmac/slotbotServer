package de.webalf.slotbot.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Alf
 * @since 06.09.2020
 */
@EqualsAndHashCode
@Entity
@Table(name = "discord_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "user_steam_id"})}, schema = "public")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User {
	/*@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<ActionLog> logs;*/

	@Id
	@Column(name = "id")
	//Workaround to ignore generated values
	private long id;

	@Column(name = "user_steam_id")
	private Long steamId64;

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
//		return getSlots().stream()
//				.map(Slot::getSquad)
//				.map(Squad::getEvent)
//				.map(Event::getDateTime)
//				.filter(dateTime -> dateTime.isBefore(LocalDateTime.now())).min(getLocalDateTimeComparator());
		return Optional.empty();
	}
}
