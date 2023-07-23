package de.webalf.slotbot.model.dtos.website.guild;

import de.webalf.slotbot.model.integration.DiscordCategory;
import lombok.Builder;

import java.util.List;

/**
 * @author Alf
 * @since 22.07.2023
 */
@Builder
public record GuildDiscordIntegrationDto(boolean connected, List<DiscordCategory> categories) {}
