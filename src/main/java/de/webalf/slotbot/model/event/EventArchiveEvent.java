package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.Event;
import lombok.Builder;
import lombok.NonNull;

/**
 * @param event          Archived event
 * @param discordGuildId Archiving guild
 * @author Alf
 * @see EventArchiveInitializedEvent
 * @since 24.07.2023
 */
@Builder
public record EventArchiveEvent(@NonNull Event event, long discordGuildId) {}
