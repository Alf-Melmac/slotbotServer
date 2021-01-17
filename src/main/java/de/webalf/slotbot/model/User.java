package de.webalf.slotbot.model;

import lombok.*;

import javax.persistence.*;

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

	public static final long DEFAULT_USER_ID = 11111;

	@Builder
	public User(long id) {
		this.id = id;
	}

	public boolean isDefaultUser() {
		return getId() == DEFAULT_USER_ID;
	}
}
