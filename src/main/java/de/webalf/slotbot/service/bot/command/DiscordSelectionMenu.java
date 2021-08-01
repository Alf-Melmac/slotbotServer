package de.webalf.slotbot.service.bot.command;

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

/**
 * @author Alf
 * @since 01.08.2021
 */
public interface DiscordSelectionMenu {
	@SuppressWarnings("unused") //Used by InteractionListener#onSelectionMenu(SelectionMenuEvent)
	void process(SelectionMenuEvent event);
}
