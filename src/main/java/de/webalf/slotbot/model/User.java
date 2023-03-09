package de.webalf.slotbot.model;

import de.webalf.slotbot.util.DateUtils;
import jakarta.persistence.*;
import lombok.*;

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
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "discord_user",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}), @UniqueConstraint(columnNames = {"user_steam_id"})},
		schema = "public")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User extends AbstractDiscordIdEntity {
	@Column(name = "user_steam_id")
	private Long steamId64;

	@Column(name = "user_external_calendar", nullable = false)
	private boolean externalCalendarIntegrationActive = false;

	@OneToMany(mappedBy = "user")
	private Set<Slot> slots = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<GuildUsers> guilds = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<GlobalRole> globalRoles = new HashSet<>();

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
				.filter(dateTime -> dateTime.isBefore(DateUtils.now()))
				.min(getLocalDateTimeComparator());
	}

	public long countParticipatedEvents() {
		return getSlots().stream().filter(slot -> slot.getEvent().getDateTime().isBefore(DateUtils.now())).count();
	}

	public List<Event> getSlottedEvents() {
		return getSlots().stream().map(Slot::getEvent).toList();
	}

	public Set<Guild> getGuilds() {
		return guilds.stream().map(GuildUsers::getGuild).collect(Collectors.toUnmodifiableSet());
	}
}
