package de.webalf.slotbot.model.dtos.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 08.06.2023
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public abstract class AbstractSquadApiDto {
	@NotBlank
	@Size(max = TEXT)
	private String name;

	private String reservedForGuildId;
}
