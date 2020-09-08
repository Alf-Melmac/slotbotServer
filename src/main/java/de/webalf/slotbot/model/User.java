package de.webalf.slotbot.model;

import lombok.*;

import javax.persistence.*;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})}, schema = "public")
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

	@Builder
	public User(long id) {
		this.id = id;
	}
}
