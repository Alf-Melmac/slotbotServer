package de.webalf.slotbot.model.event;

/**
 * Notifies about a user being banned
 *
 * @author Alf
 * @since 24.10.2024
 */
public record BanEvent(long userId, Long guildId) {}
