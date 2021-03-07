package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.twoArguments;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
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
