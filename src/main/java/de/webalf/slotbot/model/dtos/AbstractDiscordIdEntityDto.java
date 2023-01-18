package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 17.01.2023
 */
@Data
@RequiredArgsConstructor
@SuperBuilder
public class AbstractDiscordIdEntityDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	@NotBlank
	protected String id;
}
