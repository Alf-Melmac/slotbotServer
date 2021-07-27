package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.annotations.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * @author Alf
 * @since 18.07.2021
 */
public interface DiscordSlashCommand {
	void execute(SlashCommandEvent event);

	/**
	 * List of all slash command options. For each slash command the index in this list is specified in {@link SlashCommand#optionPosition()}
	 *
	 * @return list of every option of all slash commands
	 */
	@SuppressWarnings("unused") //Used by SlashCommandsService#getOptions(Class, int)
	List<OptionData> getOptions(int optionPosition);
}
