package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractDiscordIdEntityDto;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 17.01.2023
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class GuildDetailsDto extends AbstractDiscordIdEntityDto {
	boolean advanced;
	@Size(max = TEXT)
	String baseUrl;
	@Size(max = TEXT)
	String groupIdentifier;
	String emojiUrl;
}
