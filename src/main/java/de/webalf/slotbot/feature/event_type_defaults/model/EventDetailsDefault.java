package de.webalf.slotbot.feature.event_type_defaults.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT_DB;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Entity
@Table(name = "event_details_default", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"event_details_default_event_type", "event_details_default_guild"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Deprecated
public class EventDetailsDefault extends AbstractSuperIdEntity {
	/**
	 * @see EventType#getName()
	 */
	@Column(name = "event_details_default_event_type_name", length = TEXT_DB)
	@NotBlank
	@Size(max = TEXT)
	private String eventTypeName;

	@ManyToOne
	@JoinColumn(name = "event_details_default_guild", foreignKey = @ForeignKey(name = "event_details_default_guild_fk"))
	private Guild guild;
}
