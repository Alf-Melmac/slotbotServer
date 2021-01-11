package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.bot.BotService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static de.webalf.slotbot.util.bot.MessageUtils.sendDmWithoutMessage;

/**
 * Util service to work with {@link Message}s
 *
 * @author Alf
 * @since 02.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageHelper {
	private final DiscordProperties discordProperties;
	private final BotService botService;

	public void sendDmToRecipient(User recipient, String messageText) {
		botService.getJda().retrieveUserById(recipient.getId()).queue(
				user -> sendDmWithoutMessage(user, messageText)
		);
	}

	public void deleteBotDm(Message...messages) {
		Arrays.stream(messages).forEach(message -> {
			if (message.getAuthor().getIdLong() != discordProperties.getId()) {
				return;
			}
			message.delete().queue();
		});
	}
}
