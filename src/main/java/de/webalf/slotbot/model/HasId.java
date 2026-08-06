package de.webalf.slotbot.model;

/**
 * Marks an entity that exposes a {@code long} id. Temporary solution until {@link AbstractDiscordIdEntity} is removed
 * and everything is an {@link AbstractSuperIdEntity}
 *
 * @author Alf
 * @since 09.07.26
 */
public interface HasId {
	long getId();
}
