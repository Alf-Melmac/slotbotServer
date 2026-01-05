package de.webalf.slotbot.feature.event_type_defaults.model;

import de.webalf.slotbot.model.AbstractSuperIdEntity;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.enums.EventDetailType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE_DB;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Entity
@Table(name = "event_detail_default", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventDetailDefault extends AbstractSuperIdEntity {
	@Column(name = "event_detail_default_title", length = EMBEDDABLE_TITLE_DB)
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private String title;

	@Column(name = "event_detail_default_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private EventDetailType type;

	@ElementCollection
	@CollectionTable(name = "event_detail_default_selection",
			joinColumns = @JoinColumn(name = "event_detail_default_id"),
			uniqueConstraints = {@UniqueConstraint(name = "detail_default_selection_unique", columnNames = {"event_detail_default_id", "selection"})})
	private List<String> selection;

	@Column(name = "event_detail_default_text", columnDefinition = "text")
	private String text;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_type_id", foreignKey = @ForeignKey(name = "event_detail_default_event_type_fk"))
	private EventType eventType;
}
