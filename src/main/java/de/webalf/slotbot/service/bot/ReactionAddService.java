package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.bot.SwapRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.model.bot.SwapRequest.findByMessageId;
import static de.webalf.slotbot.service.bot.command.event.Swap.cleanUp;
import static de.webalf.slotbot.util.bot.MessageUtils.sendDmToRecipient;

/**
 * @author Alf
 * @since 15.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ReactionAddService {
	private final SlotBotService slotBotService;

	public void onSwapReaction(@NonNull PrivateMessageReactionAddEvent event) {
		long messageId = event.getMessageIdLong();
		long userId = event.getUserIdLong();

		final SwapRequest request = findByMessageId(messageId);

		if (request == null) {
			log.warn("Received reaction to not existing swap request");
		} else if (request.getRequestedSlotUser().getId() != userId) {
			log.warn("Received reaction from person that isn't the requester. How is this possible in DMs?");
		} else {
			final String reaction = event.getReactionEmote().getAsCodepoints();

			if (reaction.equalsIgnoreCase(THUMBS_UP)) {
				slotBotService.performDetachedSwap(request.getRequesterSlot(), request.getForeignerSlot());
			} else if (reaction.equalsIgnoreCase(THUMBS_DOWN)) {
				final JDA jda = event.getJDA();
				sendDmToRecipient(event, request.getRequester().getId(), jda.retrieveUserById(request.getRequestedSlotUser().getId()).complete().getAsMention() + " hat deine Anfrage zum Slot tauschen abgelehnt.");
				sendDmToRecipient(event, request.getRequestedSlotUser().getId(), "Du hast das Tauschangebot von " + jda.retrieveUserById(request.getRequester().getId()).complete().getAsMention() + " abgelehnt.");
			} else { //Ignore other reactions
				return;
			}
		}

		cleanUp(request, event.getChannel());
	}
}
