package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.enums.Language;
import lombok.Builder;
import lombok.Value;

/**
 * @author Alf
 * @since 12.02.2023
 */
@Value
@Builder
public class GuildConfigDto {
	Language language;
}
