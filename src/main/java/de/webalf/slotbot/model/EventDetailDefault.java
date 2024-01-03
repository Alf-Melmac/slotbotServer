package de.webalf.slotbot.model;

import de.webalf.slotbot.model.enums.EventFieldType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Entity
@Table(name = "event_detail_default", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventDetailDefault extends AbstractSuperIdEntity {
	@Column(name = "event_detail_default_title", length = EMBEDDABLE_TITLE_DB)
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private String title;

	@Column(name = "event_detail_default_type")
	@Enumerated(EnumType.STRING)
	@NonNull
	private EventFieldType type;

	@ElementCollection
	@CollectionTable(name = "event_detail_default_selection", joinColumns = @JoinColumn(name = "event_detail_default_id"))
	private List<String> selection;

	@Column(name = "event_detail_default_text", length = EMBEDDABLE_VALUE_DB)
	@Size(max = EMBEDDABLE_VALUE)
	private String text;

	@ManyToOne(targetEntity = EventDetailsDefault.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "event_details_default_id")
	private EventDetailsDefault eventDetailsDefault;
}
