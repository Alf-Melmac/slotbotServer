package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.SwapRequest;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.SlotBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.webalf.slotbot.constant.Emojis.THUMBS_DOWN;
import static de.webalf.slotbot.constant.Emojis.THUMBS_UP;
import static de.webalf.slotbot.util.bot.DiscordUserUtils.getPrivateChannel;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.sendDm;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getUserOption;

/**
 * @author Alf
 * @since 13.01.2021
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.swap",
		description = "bot.slash.event.swap.description",
		authorization = Permission.MESSAGE_HISTORY,
		optionPosition = 0)
public class Swap implements DiscordSlashCommand {
	private final EventBotService eventBotService;
	private final SlotBotService slotBotService;

	public static final Set<SwapRequest> requests = new HashSet<>();

	private static final String OPTION_SWAP_USER = "bot.slash.event.swap.option.user";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.USER, OPTION_SWAP_USER, "bot.slash.event.swap.option.user.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: swap");

		@SuppressWarnings("ConstantConditions") //Required option
		final String userId = LongUtils.toString(getUserOption(event, OPTION_SWAP_USER));

		final List<Slot> swapSlots = eventBotService.findSwapSlots(event.getChannel().getIdLong(), event.getUser().getId(), userId);
		final Slot requesterSlot = swapSlots.get(0);
		final Slot foreignSlot = swapSlots.get(1);

		final net.dv8tion.jda.api.entities.User requesterUser = event.getUser();
		final String requesterId = requesterUser.getId();

		if (foreignSlot.isEmpty()) {
			eventBotService.slot(event.getChannel().getIdLong(), foreignSlot.getNumber(), requesterId);
		} else {
			final User requester = requesterSlot.getUser();
			final User foreignSlotUser = foreignSlot.getUser();

			if (requester.getId() != requesterUser.getIdLong()) {
				//This should never happen if backend doesn't change its output
				log.warn("OwnSlotUser is not messageAuthor on swap. OwnSlotId: " + requesterSlot.getId() + " Message author id: " + requesterId);
				reply(event, locale.t("bot.slash.event.swap.response.backendError"));
				return;
			} else if (requester.equals(foreignSlotUser)) {
				reply(event, locale.t("bot.slash.event.swap.response.ownSlot"));
				return;
			} else if (SwapRequest.containsRequester(requester)) {
				reply(event, locale.t("bot.slash.event.swap.response.pending"));
				return;
			} else if (!reversedRequestExists(requesterSlot, foreignSlot, requester, requesterUser, foreignSlotUser)) {
				//noinspection DataFlowIssue Guild only command
				sendDm(
						event.getGuild().retrieveMemberById(foreignSlotUser.getId()).complete().getUser(),
						locale.t("bot.dm.swapRequest", requesterUser.getAsMention(), requesterSlot.getName(), requesterSlot.getNumber(), requesterSlot.getEvent().getName(), foreignSlot.getName(), foreignSlot.getNumber()),
						dmMessage -> {
							requests.add(new SwapRequest(requesterSlot, foreignSlot, dmMessage.getIdLong()));

							dmMessage.addReaction(THUMBS_UP.getEmoji()).queue(unused ->
									dmMessage.addReaction(THUMBS_DOWN.getEmoji()).queue());
						},
						false,
						reply -> reply(event, reply)
				);
			}
			finishedInteraction(event);
		}
	}

	/**
	 * Checks if a request in the opposite direction already exists. If it exists the swap gets performed with {@link SlotBotService#performDetachedSwap(Slot, Slot)}
	 *
	 * @return true if the reversed swap has been performed
	 */
	private boolean reversedRequestExists(Slot requesterSlot, Slot foreignSlot, User requester, net.dv8tion.jda.api.entities.User requesterUser, User foreignSlotUser) {
		final Optional<SwapRequest> request = SwapRequest.findByRequesterAndRequested(foreignSlotUser, requester);
		if (request.isPresent()) { //Reversed request already exists
			slotBotService.performDetachedSwap(foreignSlot, requesterSlot);
			cleanUp(request.get(), getPrivateChannel(requesterUser));
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
	public static void cleanUp(SwapRequest request, MessageChannel channel) {
		requests.remove(request);                               //Swap request is no longer valid
		deleteMessagesInstant(channel, request.getMessageId()); //Delete swap request dm
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
