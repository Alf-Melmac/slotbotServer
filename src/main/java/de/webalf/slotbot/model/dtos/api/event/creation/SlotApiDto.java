package de.webalf.slotbot.model.dtos.api.event.creation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.model.User.DEFAULT_USER_ID;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 14.04.2021
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class SlotApiDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	int number;

	@Schema(format = "discord-snowflake", description = "Reservation of a slot. If the squad is already reserved, this value is inherited unless overwritten here.")
	String reservedForGuildId;

	@Schema(format = "discord-snowflake")
	String userId;

	@Size(max = TEXT)
	@Schema(description = "Text to be displayed instead of a user. Only works if `userId` is set to `" + DEFAULT_USER_ID + "` at the same time.")
	String replacementText;
}
