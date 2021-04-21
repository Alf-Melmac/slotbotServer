package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	@Column(name = "event_field_name", length = (int) (MessageEmbed.TITLE_MAX_LENGTH * 1.25))
	@NotBlank
	@Size(max = MessageEmbed.TITLE_MAX_LENGTH)
	private String title;

	@Column(name = "event_field_text", length = (int) (MessageEmbed.VALUE_MAX_LENGTH * 1.25))
	@NotBlank
	@Size(max = MessageEmbed.VALUE_MAX_LENGTH)
	private String text;

	@ManyToOne(targetEntity = Event.class/*, fetch = FetchType.LAZY*/)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;
}
