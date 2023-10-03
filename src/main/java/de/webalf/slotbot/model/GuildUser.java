package de.webalf.slotbot.model;

import de.webalf.slotbot.util.permissions.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author Alf
 * @since 17.01.2022
 */
@Entity
@Table(name = "guild_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"guild_user_guild", "guild_user_user"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildUser {
	@EmbeddedId
	private GuildUserId id;

	@ManyToOne(targetEntity = Guild.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_user_guild", nullable = false)
	@NotNull
	@MapsId("guildId")
	private Guild guild;

	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_user_user", nullable = false)
	@NotNull
	@MapsId("userId")
	private User user;

	@Column(name = "guild_user_role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private GuildUser(Guild guild, User user, Role role) {
		this.id = GuildUserId.builder().guildId(guild.getId()).userId(user.getId()).build();
		this.guild = guild;
		this.user = user;
		this.role = role;
	}

	public String getApplicationRole() {
		return getRole() != null ? getRole().getApplicationRole() : null;
	}
}
