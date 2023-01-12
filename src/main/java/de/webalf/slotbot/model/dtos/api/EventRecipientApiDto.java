package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

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
