package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.bot.BotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.bot.MessageUtils.sendDm;

/**
 * Util service to work with {@link Message}s
 *
 * @author Alf
 * @since 02.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageHelper {
	private final BotService botService;

	public void sendDmToRecipient(@NonNull User recipient, String messageText) {
		sendDmToRecipient(recipient.getId(), messageText);
	}

	private void sendDmToRecipient(@NonNull long recipientId, String messageText) {
		botService.getJda().retrieveUserById(recipientId).queue(
				user -> sendDm(user, messageText)
		);
	}
}
