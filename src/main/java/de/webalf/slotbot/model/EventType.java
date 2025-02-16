package de.webalf.slotbot.model;

import de.webalf.slotbot.feature.requirement.model.RequirementList;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * The ID of this entity does not matter. The main unique key is the name and the color.
 * Entities are to be found and created using these values.
 *
 * @author Alf
 * @since 07.04.2021
 */
@Entity
@Table(name = "event_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"event_type_name", "event_color", "event_type_guild"}, name = "event_type_name_color_guild_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventType extends AbstractSuperIdEntity {
	@Column(name = "event_type_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@Column(name = "event_color", length = HEX_COLOR_DB)
	@NotBlank
	@Size(min = HEX_COLOR, max = HEX_COLOR)
	@Pattern(regexp = HEX_COLOR_PATTERN)
	private String color;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "event_type_guild")
	@NotNull
	private Guild guild;

	@ManyToMany
	@JoinTable(name = "event_type_requirement_list",
			joinColumns = @JoinColumn(name = "event_type_id", foreignKey = @ForeignKey(name = "event_type_fk")),
			inverseJoinColumns = @JoinColumn(name = "requirement_list_id", foreignKey = @ForeignKey(name = "requirement_list_fk")))
	@OrderBy("name")
	private List<RequirementList> requirementList;

	@OneToMany(mappedBy = "eventType")
	private List<Event> events;

	/**
	 * Removes the given requirement list from the list of requirement lists
	 *
	 * @param requirementList to remove
	 */
	public void removeRequirementList(RequirementList requirementList) {
		this.requirementList.remove(requirementList);
	}
}
