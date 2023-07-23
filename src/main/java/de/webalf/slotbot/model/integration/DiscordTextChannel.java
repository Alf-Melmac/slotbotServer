package de.webalf.slotbot.model.integration;

import lombok.Builder;

/**
 * @author Alf
 * @since 22.07.2023
 */
@Builder
public record DiscordTextChannel(String id, String name) {}
