package de.webalf.slotbot.model.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author Alf
 * @since 11.09.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class EventRecipientDto extends EventDto {
	@NotNull
	private UserDto recipient;

	@Builder(builderMethodName = "recipientBuilder")
	public EventRecipientDto(UserDto recipient) {
		this.recipient = recipient;
	}
}
