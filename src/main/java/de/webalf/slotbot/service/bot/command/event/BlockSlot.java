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
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalStringOption;

/**
 * @author Alf
 * @since 12.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.blockSlot",
		description = "bot.slash.event.blockSlot.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
public class BlockSlot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.blockSlot.option.slotNumber";
	private static final String OPTION_REPLACEMENT = "bot.slash.event.blockSlot.option.replacement";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.blockSlot.option.slotNumber.description", true),
					new TranslatableOptionData(OptionType.STRING, OPTION_REPLACEMENT, "bot.slash.event.blockSlot.option.replacement.description", false))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: blockSlot");

		final int slotNumber = getIntegerOption(event, OPTION_SLOT_NUMBER);
		final String replacementText = getOptionalStringOption(event, OPTION_REPLACEMENT);
		eventBotService.blockSlot(event.getChannel().getIdLong(), slotNumber, replacementText);

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
