package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.regex.Pattern;

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

	@OneToMany(mappedBy = "eventType")
	private List<Event> events;

	public void setColor(@NotNull String color) {
		String parsedColor = (color.startsWith("#") ? color : "#" + color).toLowerCase();
		if (!HEX_COLOR.matcher(parsedColor).matches()) {
			throw BusinessRuntimeException.builder().title(parsedColor + " is not a valid hex color.").build();
		}
		this.color = parsedColor;
	}

	private static final Pattern HEX_COLOR = Pattern.compile("^#([a-f\\d]{6}|[a-f\\d]{3})$");
}
