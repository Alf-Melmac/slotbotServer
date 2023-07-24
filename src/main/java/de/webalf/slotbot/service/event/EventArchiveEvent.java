package de.webalf.slotbot.service.event;

import de.webalf.slotbot.model.Event;
import lombok.Builder;
import lombok.NonNull;

/**
 * @author Alf
 * @since 24.07.2023
 */
@Builder
public record EventArchiveEvent(@NonNull Event event, long guildId) {}
