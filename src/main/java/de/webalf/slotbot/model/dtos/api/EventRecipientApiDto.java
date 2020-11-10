package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.UserDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author Alf
 * @since 11.09.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class EventRecipientApiDto extends EventDto {
	@NotNull
	private UserDto recipient;

	@Builder(builderMethodName = "recipientBuilder")
	public EventRecipientApiDto(UserDto recipient) {
		this.recipient = recipient;
	}
}
