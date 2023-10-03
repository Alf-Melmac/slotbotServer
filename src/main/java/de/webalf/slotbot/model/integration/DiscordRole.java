package de.webalf.slotbot.model.integration;

import lombok.Builder;

/**
 * @author Alf
 * @since 03.10.2023
 */
@Builder
public record DiscordRole(String id, String name) {}
