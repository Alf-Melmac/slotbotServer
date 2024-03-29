package de.webalf.slotbot.model;

import de.webalf.slotbot.util.permissions.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 17.08.2022
 */
@Entity
@Table(name = "global_roles", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class GlobalRole extends AbstractSuperIdEntity {
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "global_roles_user")
	@NonNull
	private User user;

	@Column(name = "global_roles_role")
	@Enumerated(EnumType.STRING)
	@NotBlank
	private Role role;
}
