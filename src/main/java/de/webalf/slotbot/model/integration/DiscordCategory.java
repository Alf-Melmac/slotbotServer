package de.webalf.slotbot.model.integration;

import lombok.Builder;

import java.util.List;

/**
 * @author Alf
 * @since 22.07.2023
 */
@Builder
public record DiscordCategory(String name, List<DiscordTextChannel> textChannels) {}
