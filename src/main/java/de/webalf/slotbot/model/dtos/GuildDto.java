package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @param id String is needed because internal ids can be discord IDs that exceed the maximum size of a JavaScript number
 * @author Alf
 * @since 16.01.2022
 */
@Builder
public record GuildDto(
		String id,
		@Size(max = TEXT) String groupIdentifier,
		String emojiUrl
) {}
