package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
