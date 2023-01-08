package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.Command;
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
 * @since 19.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"renameSquad", "editSquad", "eventRenameSquad"},
		description = "Ermöglicht es einen Squad umzubenennen. Squads sind durchnummeriert, beginnend mit 0.",
		usage = "<Squad Position> \"<Squadname>\"",
		argCount = {2},
		authorization = EVENT_MANAGE)
public class RenameSquad implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: renameSquad");

		final String squadPosition = args.get(0);
		if (!onlyNumbers(squadPosition)) {
			replyAndDelete(message, "Die Squad Position muss eine Zahl sein.");
			return;
		}

		eventBotService.renameSquad(message.getChannel().getIdLong(), Integer.parseInt(squadPosition), args.get(1));
		deleteMessagesInstant(message);
	}
}
