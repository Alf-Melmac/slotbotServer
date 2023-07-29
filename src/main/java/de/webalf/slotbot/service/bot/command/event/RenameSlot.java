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
 * @since 12.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.renameSlot",
		description = "bot.slash.event.renameSlot.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
public class RenameSlot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.renameSlot.option.slotNumber";
	private static final String OPTION_NAME = "bot.slash.event.renameSlot.option.name";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.renameSlot.option.slotNumber.description", true),
					new TranslatableOptionData(OptionType.STRING, OPTION_NAME, "bot.slash.event.renameSlot.option.name.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: renameSlot");

		final int slotNumber = getIntegerOption(event, OPTION_SLOT_NUMBER);
		final String name = getStringOption(event, OPTION_NAME);
		eventBotService.renameSlot(event.getChannel().getIdLong(), slotNumber, name);

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
