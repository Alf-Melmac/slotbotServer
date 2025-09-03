package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.dtos.GuildDto;

import java.util.List;

/**
 * @author Alf
 * @since 03.09.2025
 */
public record GuildsCategorisedDto(List<GuildDto> active, List<GuildDto> inactive) {}
