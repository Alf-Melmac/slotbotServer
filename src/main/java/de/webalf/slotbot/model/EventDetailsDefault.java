package de.webalf.slotbot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT_DB;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Entity
@Table(name = "event_details_default", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"event_details_default_event_type_name", "event_details_default_guild"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventDetailsDefault extends AbstractSuperIdEntity {
	/**
	 * @see EventType#getName()
	 */
	@Column(name = "event_details_default_event_type_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String eventTypeName;

	@ManyToOne(targetEntity = Guild.class)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "event_details_default_guild")
	private Guild guild;

	@OneToMany(mappedBy = "eventDetailsDefault", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventDetailDefault> eventFieldDefaults;
}
