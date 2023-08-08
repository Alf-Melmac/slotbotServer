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
import static de.webalf.slotbot.util.bot.SlashCommandUtils.*;

/**
 * @author Alf
 * @since 11.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.addSlot",
		description = "bot.slash.event.addSlot.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
public class AddSlot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SQUAD_POSITION = "bot.slash.event.addSlot.option.squadPosition";
	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.addSlot.option.slotNumber";
	private static final String OPTION_NAME = "bot.slash.event.addSlot.option.name";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SQUAD_POSITION, "bot.slash.event.addSlot.option.squadPosition.description", true),
					new TranslatableOptionData(OptionType.STRING, OPTION_NAME, "bot.slash.event.addSlot.option.name.description", true),
					new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.addSlot.option.slotNumber.description", false))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: addSlot");

		final int squadPosition = getIntegerOption(event, OPTION_SQUAD_POSITION);
		final String name = getStringOption(event, OPTION_NAME);
		final Integer slotNumber = getOptionalIntegerOption(event, OPTION_SLOT_NUMBER);
		eventBotService.addSlot(event.getChannel().getIdLong(), squadPosition, slotNumber, name);

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
