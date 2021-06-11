package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Alf
 * @since 07.04.2021
 */
@Entity
@Table(name = "event_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class EventType extends AbstractSuperIdEntity {
	@Column(name = "event_type_name", length = 100)
	@NotBlank
	@Size(max = 80)
	private String name;

	@Column(name = "event_color", length = 7)
	@NotBlank
	@Size(max = 7) //Expected format: #rrggbb
	private String color;

	@OneToMany(mappedBy = "eventType")
	private List<Event> events;

	public void setColor(String color) {
		if (!HEX_COLOR.matcher(color).matches()) {
			throw BusinessRuntimeException.builder().title(color + " is not a valid hex color.").build();
		}

		this.color = color.startsWith("#") ? color : "#" + color;
	}

	private static final Pattern HEX_COLOR = Pattern.compile("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
}
