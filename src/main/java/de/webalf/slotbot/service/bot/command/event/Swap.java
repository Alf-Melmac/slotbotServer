package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.model.bot.SwapRequest;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.SlotBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.DiscordUserUtils.getPrivateChannel;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;
import static de.webalf.slotbot.util.bot.MentionUtils.*;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getUserOption;
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
@SlashCommand(name = "swap",
		description = "Sendet eine Anfrage, um einen Slot mit einer Person zu tauschen.",
		authorization = NONE,
		optionPosition = 0)
public class Swap implements DiscordCommand, DiscordSlashCommand {
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

		swap(swapSlots, channel, requesterId, (String reply) -> replyAndDeleteOnlySend(message, reply), message.getJDA(),
				() -> deleteMessagesInstant(message), true);
	}

	private void swap(@NonNull List<Slot> swapSlots,
	                  long channel,
	                  String requesterId,
	                  @NonNull Consumer<String> replyConsumer,
	                  @NonNull JDA jda,
	                  @NonNull Runnable finishCallback,
	                  boolean forceFinishCall) {
		final Slot requesterSlot = swapSlots.get(0);
		final Slot foreignSlot = swapSlots.get(1);

		if (foreignSlot.isEmpty()) {
			eventBotService.slot(channel, foreignSlot.getNumber(), requesterId);
		} else {
			final User requester = requesterSlot.getUser();
			final User foreignSlotUser = foreignSlot.getUser();

			if (requester.getId() != LongUtils.parseLong(requesterId)) {
				//This should never happen if backend doesn't change its output
				log.warn("OwnSlotUser is not messageAuthor on swap. OwnSlotId: " + requesterSlot.getId() + " Message author id: " + requesterId);
				replyConsumer.accept("Da ist ein Reihenfolgeproblem aufgetreten. Versuche es nochmal oder kontaktiere einen Administrator.");
				if (!forceFinishCall) return;
			} else if (requester.equals(foreignSlotUser)) {
				replyConsumer.accept("Du bist schon auf dem Slot.");
				if (!forceFinishCall) return;
			} else if (SwapRequest.containsRequester(requester)) {
				replyConsumer.accept("Es besteht bereits eine Tausch-Anfrage.");
				if (!forceFinishCall) return;
			} else if (!reversedRequestExists(jda, requesterSlot, foreignSlot, requester, foreignSlotUser)) {
				sendDm(
						jda.retrieveUserById(foreignSlotUser.getId()).complete(),
						getUserAsMention(requesterId) + " würde gerne den Slot " + requesterSlot.getName() + " (" + requesterSlot.getNumber() + ") im Event " + requesterSlot.getEvent().getName() + " mit dir tauschen. Du bist aktuell als " + foreignSlot.getName() + " (" + foreignSlot.getNumber() + ") gelistet. \nReagiere mit 👍, um die Anfrage anzunehmen. 👎 dementsprechend, um sie abzulehnen.",
						dmMessage -> {
							requests.add(new SwapRequest(requesterSlot, foreignSlot, dmMessage.getIdLong()));

							dmMessage.addReaction(THUMBS_UP).queue(unused ->
									dmMessage.addReaction(THUMBS_DOWN).queue());
						},
						false,
						replyConsumer
				);
			}
			finishCallback.run();
		}
	}

	/**
	 * Checks if a request in the opposite direction already exists. If it exists the swap gets performed with {@link SlotBotService#performDetachedSwap(Slot, Slot)}
	 *
	 * @return true if the reversed swap has been performed
	 */
	private boolean reversedRequestExists(JDA jda, Slot requesterSlot, Slot foreignSlot, @NonNull User requester, User foreignSlotUser) {
		final Optional<SwapRequest> request = SwapRequest.findByRequesterAndRequested(foreignSlotUser, requester);
		if (request.isPresent()) { //Reversed request already exists
			slotBotService.performDetachedSwap(foreignSlot, requesterSlot);
			cleanUp(request.get(), getPrivateChannel(jda, requester.getId()));
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

	private static final String OPTION_SWAP_USER = "tauschperson";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.USER, OPTION_SWAP_USER, "Person mit der getauscht werden soll.", true))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: swap");

		@SuppressWarnings("ConstantConditions") //Required option
		final String userId = LongUtils.toString(getUserOption(event.getOption(OPTION_SWAP_USER)));

		List<Slot> swapSlots = eventBotService.findSwapSlots(event.getChannel().getIdLong(), event.getUser().getId(), userId);

		swap(swapSlots, event.getChannel().getIdLong(), event.getUser().getId(), reply -> reply(event, reply), event.getJDA(),
				() -> finishedSlashCommandAction(event), false);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
