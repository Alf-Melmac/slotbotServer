package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.model.enums.Language;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author Alf
 * @since 12.02.2023
 */
@Value
@Builder
public class GuildConfigDto {
	Language language;
	List<EventTypeDto> eventTypes;
}
