package de.webalf.slotbot.feature.requirement.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 14.11.2024
 */
@Entity
@Table(name = "requirement", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"requirement_name", "requirement_list_id"}, name = "requirement_name_list_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Requirement extends AbstractSuperIdEntity {
	@Column(name = "requirement_name", length = TEXT_DB, nullable = false)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@Column(name = "requirement_icon", length = URL_DB)
	@Size(max = URL)
	@Pattern(regexp = URL_PATTERN)
	private String icon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requirement_list_id")
	@NotNull
	private RequirementList requirementList;
}
