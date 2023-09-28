package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
	private Set<GuildUser> guilds = new HashSet<>();

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

	public Set<Guild> getGuilds() {
		return guilds.stream().map(GuildUser::getGuild).collect(Collectors.toUnmodifiableSet());
	}
}
