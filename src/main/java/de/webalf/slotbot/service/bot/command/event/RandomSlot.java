package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

/**
 * @author Alf
 * @since 31.03.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"randomSlot", "slotRandom", "random"},
		description = "Trägt dich auf einem zufälligen Slot im Event ein.",
		authorization = NONE)
public class RandomSlot implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: randomSlot");

		eventBotService.randomSlot(message.getChannel().getIdLong(), message.getAuthor().getId());
		deleteMessagesInstant(message);
	}
}
