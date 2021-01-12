package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

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
@Command(name = "delslot",
		description = "Entfernt einen leeren Slot aus einem Event.",
		usage = "<Slotnummer>",
		argCount = {1},
		authorization = EVENT_MANAGE)
public class DelSlot implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: delslot");

		final String slotNumber = args.get(0);
		if (!onlyNumbers(slotNumber)) {
			replyAndDelete(message, "Die Slotnummer muss eine Zahl sein.");
			return;
		}

		eventBotService.delSlot(message.getChannel().getIdLong(), Integer.parseInt(slotNumber));
		deleteMessagesInstant(message);
	}
}
