package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 16.01.2022
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public class GuildDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	private String id;

	@Size(max = TEXT)
	private String groupIdentifier;

	private String emojiUrl;
}
