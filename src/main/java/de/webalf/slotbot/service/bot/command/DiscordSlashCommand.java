package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

/**
 * @author Alf
 * @since 18.07.2021
 */
public interface DiscordSlashCommand {
	@SuppressWarnings("unused") //Used by InteractionListener#onSlashCommandInteraction(SlashCommandInteractionEvent)
	void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale);

	/**
	 * List of all slash command options. For each slash command the index in this list is specified in {@link SlashCommand#optionPosition()}
	 *
	 * @return list of every option of all slash commands
	 */
	@SuppressWarnings("unused") //Used by CommandsService#getOptions(Class, int, Map)
	default List<TranslatableOptionData> getOptions(int optionPosition) {
		return Collections.emptyList();
	}
}
