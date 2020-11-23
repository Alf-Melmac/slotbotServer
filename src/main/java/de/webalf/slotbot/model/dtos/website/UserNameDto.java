package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.UserDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 22.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class UserNameDto extends UserDto {
	String name;
}
