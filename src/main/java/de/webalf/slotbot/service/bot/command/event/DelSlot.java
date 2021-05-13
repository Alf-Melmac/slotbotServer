package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
@Command(names = {"delSlot", "eventDelSlot", "deleteSlot", "removeSlot", "slotDel", "slotRemove"},
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
