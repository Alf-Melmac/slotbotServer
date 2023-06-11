package de.webalf.slotbot.model.dtos.api.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

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

	@Schema(format = "discord-snowflake")
	private String reservedForGuildId;
}
