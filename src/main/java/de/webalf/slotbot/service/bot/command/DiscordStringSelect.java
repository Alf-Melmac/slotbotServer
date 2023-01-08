package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author Alf
 * @since 01.08.2021
 */
public interface DiscordStringSelect {
	@SuppressWarnings("unused") //Used by InteractionListener#onStringSelectInteraction(StringSelectInteractionEvent)
	void process(@NonNull StringSelectInteractionEvent event, @NonNull DiscordLocaleHelper locale);
}
