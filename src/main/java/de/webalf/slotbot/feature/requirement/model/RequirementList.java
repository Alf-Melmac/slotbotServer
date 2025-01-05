package de.webalf.slotbot.feature.requirement.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import de.webalf.slotbot.model.Guild;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT_DB;

/**
 * @author Alf
 * @since 14.11.2024
 */
@Entity
@Table(name = "requirement_list", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"requirement_list_name", "requirement_list_guild"}, name = "requirement_list_name_guild_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class RequirementList extends AbstractSuperIdEntity {
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "requirement_list_guild")
	private Guild guild;

	@Column(name = "requirement_list_name", length = TEXT_DB, nullable = false)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@OneToMany(mappedBy = "requirementList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("name")
	private List<Requirement> requirements;

	/**
	 * Whether members themselves can decide whether they fulfil a requirement
	 */
	@Column(name = "requirement_list_member_assignable", nullable = false)
	@Builder.Default
	private boolean memberAssignable = true;

	/**
	 * Whether the requirements are recommendations or enforced
	 */
	@Column(name = "requirement_list_enforced", nullable = false)
	@Builder.Default
	private boolean enforced = false;

	/**
	 * Set parents in child objects
	 */
	public void setBackReferences() {
		getRequirements()
				.forEach(requirement -> requirement.setRequirementList(this));
	}
}
