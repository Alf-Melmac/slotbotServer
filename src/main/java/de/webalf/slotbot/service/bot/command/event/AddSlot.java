package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
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
 * @since 11.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@Command(names = {"addSlot", "eventAddSlot", "slotAdd", "newSlot"},
		description = "Fügt einem Event einen Slot hinzu. Squads sind durchnummeriert, beginnend mit 0.",
		usage = "<Squad Position> <Slotnummer> <Slotname>",
		argCount = {3},
		authorization = EVENT_MANAGE)
public class AddSlot implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: addslot");

		final String squadPosition = args.get(0);
		final String slotNumber = args.get(1);
		if (!onlyNumbers(squadPosition) || !onlyNumbers(slotNumber)) {
			replyAndDelete(message, "Die Squad Position und Slotnummer müssen Zahlen sein.");
			return;
		}

		eventBotService.addSlot(message.getChannel().getIdLong(), Integer.parseInt(squadPosition), Integer.parseInt(slotNumber), args.get(2));
		deleteMessagesInstant(message);
	}
}
