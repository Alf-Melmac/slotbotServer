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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedVisibleInteraction;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalIntegerOption;

/**
 * @author Alf
 * @since 11.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@SlashCommand(name = "bot.slash.event.unslot",
		description = "bot.slash.event.unslot.description",
		authorization = Permission.MESSAGE_HISTORY)
@SlashCommand(name = "bot.slash.event.unslot.force",
		description = "bot.slash.event.unslot.force.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 0)
public class Unslot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.unslot.option.number";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.unslot.option.number.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: unslot");

		final Integer slotNumber = getOptionalIntegerOption(event, OPTION_SLOT_NUMBER);
		if (slotNumber == null) { //Self unslot
			eventBotService.unslot(event.getChannel().getIdLong(), event.getUser().getId());
		} else { //Unslot others
			eventBotService.unslot(event.getChannel().getIdLong(), slotNumber);
		}

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
