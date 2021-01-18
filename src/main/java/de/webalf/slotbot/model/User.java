package de.webalf.slotbot.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static de.webalf.slotbot.util.DateUtils.getLocalDateTimeComparator;

/**
 * @author Alf
 * @since 06.09.2020
 */
@EqualsAndHashCode
@Entity
@Table(name = "discord_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})}, schema = "public")
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

	@OneToMany(mappedBy = "user")
	private Set<Slot> slots = new HashSet<>();

	public static final long DEFAULT_USER_ID = 11111;

	@Builder
	public User(long id) {
		this.id = id;
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


}
