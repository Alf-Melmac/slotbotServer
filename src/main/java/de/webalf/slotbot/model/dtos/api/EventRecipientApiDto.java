package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.UserDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * @author Alf
 * @since 11.09.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventRecipientApiDto extends EventApiDto {
	@NotNull
	UserDto recipient;
}
