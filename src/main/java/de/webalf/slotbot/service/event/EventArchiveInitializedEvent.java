package de.webalf.slotbot.service.event;

import de.webalf.slotbot.model.Event;
import lombok.Builder;
import lombok.NonNull;

/**
 * @param event        Archived event
 * @param guild        Archiving guild
 * @param discordGuild Archiving guild
 * @author Alf
 * @since 23.07.2023
 */
@Builder
public record EventArchiveInitializedEvent(@NonNull Event event,
                                           @NonNull de.webalf.slotbot.model.Guild guild,
                                           @NonNull net.dv8tion.jda.api.entities.Guild discordGuild) {
}
