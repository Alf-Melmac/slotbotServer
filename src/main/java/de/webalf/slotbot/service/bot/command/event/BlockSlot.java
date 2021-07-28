package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.twoArguments;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getIntegerOption;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalStringOption;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 12.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"blockSlot", "slotBlock", "block"},
		description = "Sperrt einen Slot und setzt, falls angegeben, den Text an dessen Stelle.",
		usage = "<Slotnummer> (\"<Ersatzname>\")",
		argCount = {1, 2},
		authorization = EVENT_MANAGE)
@SlashCommand(name = "blockSlot",
		description = "Sperrt einen Slot und setzt, falls angegeben, den Text an dessen Stelle.",
		authorization = EVENT_MANAGE,
		optionPosition = 0)
public class BlockSlot implements DiscordCommand, DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: blockslot");

		final String slotNumber = args.get(0);
		if (!onlyNumbers(slotNumber)) {
			replyAndDelete(message, "Die Slotnummer muss eine Zahl sein.");
			return;
		}

		final String replacementText = !twoArguments(args) ? null : args.get(1);

		eventBotService.blockSlot(message.getChannel().getIdLong(), Integer.parseInt(slotNumber), replacementText);
		deleteMessagesInstant(message);
	}

	private static final String OPTION_SLOT_NUMBER = "slotnummer";
	private static final String OPTION_REPLACEMENT = "ersatzname";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "Nummer des zu blockierenden Slots.", true),
					new OptionData(OptionType.STRING, OPTION_REPLACEMENT, "Ersatzname", false))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: blockSlot");

		@SuppressWarnings("ConstantConditions") //Required option
		final int slotNumber = getIntegerOption(event.getOption(OPTION_SLOT_NUMBER));
		final String replacementText = getOptionalStringOption(event.getOption(OPTION_REPLACEMENT));
		eventBotService.blockSlot(event.getChannel().getIdLong(), slotNumber, replacementText);

		finishedSlashCommandAction(event);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
