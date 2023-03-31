package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.enums.Language;
import lombok.Builder;

/**
 * @author Alf
 * @since 12.02.2023
 */
@Builder
public record GuildConfigDto(Language language) {}
