package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author Alf
 * @since 22.08.2023
 */
public interface DiscordButton {
	@SuppressWarnings("unused") //Used by InteractionListener#onButtonInteraction(ButtonInteractionEvent)
	void handle(@NonNull ButtonInteractionEvent event, @NonNull DiscordLocaleHelper locale);
}
