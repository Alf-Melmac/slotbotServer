package de.webalf.slotbot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * @author Alf
 * @since 17.01.2022
 */
@Entity
@Table(name = "guild_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class GuildUsers extends AbstractSuperIdEntity {
	@ManyToOne(targetEntity = Guild.class)
	@JoinColumn(name = "guild_users_guild")
	private Guild guild;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "guild_users_user")
	private User user;
}
