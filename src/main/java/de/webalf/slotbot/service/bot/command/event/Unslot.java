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
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalIntegerOption;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalUserOption;

/**
 * @author Alf
 * @since 11.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.unslot",
		description = "bot.slash.event.unslot.description",
		authorization = Permission.MESSAGE_HISTORY)
@SlashCommand(name = "bot.slash.event.unslot.force",
		description = "bot.slash.event.unslot.force.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
@SlashCommand(name = "bot.slash.event.unslot.force.user",
		description = "bot.slash.event.unslot.force.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 1)
public class Unslot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.unslot.option.number";
	private static final String OPTION_USER = "bot.slash.event.unslot.option.user";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.unslot.option.number.description", true)),
			List.of(new TranslatableOptionData(OptionType.USER, OPTION_USER, "bot.slash.event.unslot.option.user.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: unslot");

		final Integer slotNumber = getOptionalIntegerOption(event, OPTION_SLOT_NUMBER);
		final Long user = getOptionalUserOption(event, OPTION_USER);
		if (slotNumber != null) { //Unslot by slot number
			eventBotService.unslot(event.getChannel().getIdLong(), slotNumber);
		} else if (user != null) { //Unslot by user
			eventBotService.unslot(event.getChannel().getIdLong(), Long.toString(user));
		} else { //Self unslot
			eventBotService.unslot(event.getChannel().getIdLong(), event.getUser().getId());
		}

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
