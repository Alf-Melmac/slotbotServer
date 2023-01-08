package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.model.bot.SwapRequest;
import de.webalf.slotbot.service.bot.ReactionAddService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;

/**
 * @author Alf
 * @since 14.01.2021
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReactionAddListener extends ListenerAdapter {
	private final ReactionAddService reactionAddService;

	@Override
	public void onMessageReactionAdd(@NonNull MessageReactionAddEvent event) {
		final User user = event.getUser();
		if (user == null) {
			log.warn("Reacting user is not cached " + event.getUserId());
			return;
		} else if (user.isBot() || event.isFromGuild()) {
			return;
		}

		if (SwapRequest.containsMessageId(event.getMessageIdLong())) {
			reactionAddService.onSwapReaction(event);
		} else if (event.getChannel().retrieveMessageById(event.getMessageId()).complete()
				.getReactions().stream()
				.map(messageReaction -> messageReaction.getEmoji().getFormatted())
				.collect(Collectors.toUnmodifiableList())
				.containsAll(List.of(THUMBS_UP.getFormatted(), THUMBS_DOWN.getFormatted()))) {
			//For now, we assume this message is a stale swap request and simply delete the message. Can be removed after persisted swap requests prevent this problem
			//TODO https://trello.com/c/AP4UIpQB/341-persistiere-swap-anfragen
			deleteMessagesInstant(event.getChannel(), event.getMessageIdLong());
		}
	}
}
