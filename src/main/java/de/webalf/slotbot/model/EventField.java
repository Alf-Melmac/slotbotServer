package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.MaxLength.*;

/**
 * @author Alf
 * @since 07.04.2021
 */
@Entity
@Table(name = "event_field", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventField extends AbstractSuperIdEntity {
	@Column(name = "event_field_name", length = EMBEDDABLE_TITLE_DB)
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private String title;

	@Column(name = "event_field_text", length = EMBEDDABLE_VALUE_DB)
	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	private String text;

	@ManyToOne(targetEntity = Event.class/*, fetch = FetchType.LAZY*/)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;
}
