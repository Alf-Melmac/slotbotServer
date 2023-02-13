package de.webalf.slotbot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static de.webalf.slotbot.util.MaxLength.*;

/**
 * @author Alf
 * @since 07.04.2021
 */
@Entity
@Table(name = "event_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"event_type_name", "event_color"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventType extends AbstractSuperIdEntity {
	//The ID of this entity does not matter. The main unique key is the name and the colour.
	//Entities are to be found and created using these values.

	@Column(name = "event_type_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String name;

	@Column(name = "event_color", length = COLOR_RGB_DB)
	@NotBlank
	@Size(max = COLOR_RGB) //Expected format: #rrggbb
	private String color;

	@ManyToOne(targetEntity = Guild.class)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "event_type_guild")
	private Guild guild;

	@OneToMany(mappedBy = "eventType")
	private List<Event> events;
}
