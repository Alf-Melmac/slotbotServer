package de.webalf.slotbot.model.dtos.api.event.creation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.MaxLength.TEXT;

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

	String reservedForGuildId;

	String userId;

	@Size(max = TEXT)
	String replacementText;
}
