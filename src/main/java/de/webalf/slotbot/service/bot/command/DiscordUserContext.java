package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

/**
 * @author Alf
 * @since 11.12.2022
 */
public interface DiscordUserContext {
	@SuppressWarnings("unused") //Used by InteractionListener#onUserContextInteraction(UserContextInteractionEvent)
	void perform(@NonNull UserContextInteractionEvent event, @NonNull DiscordLocaleHelper locale);
}
