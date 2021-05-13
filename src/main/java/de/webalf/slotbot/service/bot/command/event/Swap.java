package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.bot.SwapRequest;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.SlotBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.DiscordUserUtils.getPrivateChannel;
import static de.webalf.slotbot.util.bot.MentionUtils.getId;
import static de.webalf.slotbot.util.bot.MentionUtils.isUserMention;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

/**
 * @author Alf
 * @since 13.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"swap"},
		description = "Sendet eine Anfrage, um einen Slot mit einer Person zu tauschen.",
		usage = "<Slotnummer>",
		argCount = {1},
		authorization = NONE)
public class Swap implements DiscordCommand {
	private final EventBotService eventBotService;
	private final SlotBotService slotBotService;

	public static final Set<SwapRequest> requests = new HashSet<>();

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: swap");

		final long channel = message.getChannel().getIdLong();
		final net.dv8tion.jda.api.entities.User messageAuthor = message.getAuthor();
		final String requesterId = messageAuthor.getId();

		final String arg = args.get(0);

		List<Slot> swapSlots;
		if (isUserMention(arg)) {
			swapSlots = eventBotService.findSwapSlots(channel, requesterId, getId(arg));
		} else if (onlyNumbers(arg)) {
			swapSlots = eventBotService.findSwapSlots(channel, Integer.parseInt(arg), requesterId);
		} else {
			replyAndDelete(message, "Du musst einen Mitspieler oder eine Slotnummer übergeben.");
			return;
		}

		final Slot requesterSlot = swapSlots.get(0);
		final Slot foreignSlot = swapSlots.get(1);

		if (foreignSlot.isEmpty()) {
			eventBotService.slot(channel, foreignSlot.getNumber(), requesterId);
		} else {
			final User requester = requesterSlot.getUser();
			final User foreignSlotUser = foreignSlot.getUser();

			if (requester.getId() != messageAuthor.getIdLong()) {
				//This should never happen if backend doesn't change its output
				log.warn("OwnSlotUser is not messageAuthor on swap. OwnSlotId: " + requesterSlot.getId() + " Message author id: " + messageAuthor.getId());
				replyAndDeleteOnlySend(message, "Da ist ein Reihenfolgeproblem aufgetreten. Versuche es nochmal oder kontaktiere einen Administrator.");
			} else if (requester.equals(foreignSlotUser)) {
				replyAndDeleteOnlySend(message, "Du bist schon auf dem Slot.");
			} else if (SwapRequest.containsRequester(requester)) {
				replyAndDeleteOnlySend(message, "Es besteht bereits eine Tausch-Anfrage.");
			} else if (!reversedRequestExists(message, requesterSlot, foreignSlot, requester, foreignSlotUser)) {
				sendDmToRecipient(
						message,
						foreignSlotUser.getId(),
						messageAuthor.getAsMention() + " würde gerne den Slot " + requesterSlot.getName() + " (" + requesterSlot.getNumber() + ") im Event " + requesterSlot.getEvent().getName() + " mit dir tauschen. Du bist aktuell als " + foreignSlot.getName() + " (" + foreignSlot.getNumber() + ") gelistet. \nReagiere mit 👍, um die Anfrage anzunehmen. 👎 dementsprechend, um sie abzulehnen.",
						dmMessage -> {
							requests.add(new SwapRequest(requesterSlot, foreignSlot, dmMessage.getIdLong()));

							dmMessage.addReaction(THUMBS_UP).queue(unused ->
									dmMessage.addReaction(THUMBS_DOWN).queue());
						}
				);
			}

		}

		deleteMessagesInstant(message);
	}

	/**
	 * Checks if a request in the opposite direction already exists. If it exists the swap gets performed with {@link SlotBotService#performDetachedSwap(Slot, Slot)}
	 *
	 * @return true if the reversed swap has been performed
	 */
	private boolean reversedRequestExists(@NonNull Message message, Slot requesterSlot, Slot foreignSlot, @NonNull User requester, User foreignSlotUser) {
		final Optional<SwapRequest> request = SwapRequest.findByRequesterAndRequested(foreignSlotUser, requester);
		if (request.isPresent()) { //Reversed request already exists
			slotBotService.performDetachedSwap(foreignSlot, requesterSlot);
			cleanUp(request.get(), getPrivateChannel(message.getJDA(), requester.getId()));
			return true;
		}
		return false;
	}

	/**
	 * Removes the given {@link SwapRequest} from the cache and deletes the associated private message
	 *
	 * @param request to remove
	 * @param channel in which the private message has been posted
	 */
	public static void cleanUp(SwapRequest request, PrivateChannel channel) {
		requests.remove(request);                               //Swap request is no longer valid
		deleteMessagesInstant(channel, request.getMessageId()); //Delete swap request dm
	}
}
