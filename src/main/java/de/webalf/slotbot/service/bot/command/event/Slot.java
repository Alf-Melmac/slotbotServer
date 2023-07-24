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
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalUserOption;

/**
 * @author Alf
 * @since 10.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.slot",
		description = "bot.slash.event.slot.description",
		authorization = Permission.MESSAGE_HISTORY,
		optionPosition = 0)
@SlashCommand(name = "bot.slash.event.slot.force",
		description = "bot.slash.event.slot.force.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 1)
public class Slot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.slot.option.number";
	private static final String OPTION_SLOT_USER = "bot.slash.event.slot.option.user";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.slot.option.number.description", true)),
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.slot.option.number.description", true),
					new TranslatableOptionData(OptionType.USER, OPTION_SLOT_USER, "bot.slash.event.slot.option.user.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: slot");

		final int slotNumber = getIntegerOption(event, OPTION_SLOT_NUMBER);

		final Long user = getOptionalUserOption(event, OPTION_SLOT_USER);
		if (user == null) { //Self slot
			eventBotService.slot(event.getChannel().getIdLong(), slotNumber, event.getUser().getId());
		} else { //Slot others
			eventBotService.slot(event.getChannel().getIdLong(), slotNumber, Long.toString(user));
		}

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
