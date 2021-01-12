package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.twoArguments;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;

/**
 * @author Alf
 * @since 12.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@Command(name = "blockslot",
		description = "Sperrt einen Slot und setzt, falls angegeben, den Text an dessen Stelle.",
		usage = "<Slotnummer> (\"<Ersatzname>\")",
		argCount = {1, 2},
		authorization = EVENT_MANAGE)
public class BlockSlot implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: blockslot");

		final String slotNumber = args.get(0);
		if (!onlyNumbers(slotNumber)) {
			replyAndDelete(message, "Die Slotnummer muss eine Zahl sein.");
			return;
		}

		String replacementText = null;
		if (twoArguments(args)) {
			replacementText = args.get(1);
		}

		eventBotService.blockSlot(message.getChannel().getIdLong(), Integer.parseInt(slotNumber), replacementText);
		deleteMessagesInstant(message);
	}
}
