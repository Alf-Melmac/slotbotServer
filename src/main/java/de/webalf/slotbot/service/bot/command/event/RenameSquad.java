package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedVisibleInteraction;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getIntegerOption;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getStringOption;

/**
 * @author Alf
 * @since 19.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.renameSquad",
		description = "bot.slash.event.renameSquad.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
public class RenameSquad implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SQUAD_POSITION = "bot.slash.event.renameSquad.option.squadPosition";
	private static final String OPTION_NAME = "bot.slash.event.renameSquad.option.name";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SQUAD_POSITION, "bot.slash.event.renameSquad.option.squadPosition.description", true),
					new TranslatableOptionData(OptionType.STRING, OPTION_NAME, "bot.slash.event.renameSquad.option.name.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: renameSquad");

		final int squadPosition = getIntegerOption(event, OPTION_SQUAD_POSITION);
		final String name = getStringOption(event, OPTION_NAME);
		eventBotService.renameSquad(event.getChannel().getIdLong(), squadPosition, name);

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}

}
