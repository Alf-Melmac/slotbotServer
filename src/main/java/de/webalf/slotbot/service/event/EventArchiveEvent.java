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
public record EventArchiveEvent(@NonNull Event event, de.webalf.slotbot.model.Guild guild,
                                net.dv8tion.jda.api.entities.Guild discordGuild) {
}
