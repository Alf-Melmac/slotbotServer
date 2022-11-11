package de.webalf.slotbot.model.dtos.website.event.creation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class MinimalSlotDto {
	@NotBlank
	@Size(max = TEXT)
	private final String name;

	private final int number;

	private final String reservedFor;

	private final boolean blocked;

	@Size(max = TEXT)
	private final String replacementText;
}
