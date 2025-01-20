package de.webalf.slotbot.model.dtos.website.event.creation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 25.07.2022
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MinimalSlotDto {
	@NotBlank
	@Size(max = TEXT)
	private String name;

	private int number;

	private String reservedFor;

	private Set<Long> requirements;

	private boolean blocked;

	@Size(max = TEXT)
	private String replacementText;
}
