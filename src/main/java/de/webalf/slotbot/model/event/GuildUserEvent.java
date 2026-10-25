package de.webalf.slotbot.model.event;

/**
 * Notifies about a guild user change
 *
 * @author Alf
 * @since 20.10.2026
 */
public sealed interface GuildUserEvent permits GuildUserCreatedEvent, GuildUserDeleteEvent, GuildUserRoleUpdateEvent {
	long userId();
}
