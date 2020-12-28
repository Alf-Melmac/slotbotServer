package de.webalf.slotbot.model.paging;

import lombok.Value;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author Alf
 * @since 28.12.2020
 */
@Value
public class Sort {
	String property;
	Direction direction;

	public Sort(String property, String direction) {
		this.property = property;
		this.direction = Direction.fromString(direction);
	}

	public org.springframework.data.domain.Sort getSort() {
		return org.springframework.data.domain.Sort.by(direction, property);
	}
}
