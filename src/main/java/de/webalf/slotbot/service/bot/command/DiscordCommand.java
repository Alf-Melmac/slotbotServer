package de.webalf.slotbot.service.bot.command;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * @author Alf
 * @since 01.01.2021
 */
public interface DiscordCommand {
	void execute(Message message, List<String> args);
}
