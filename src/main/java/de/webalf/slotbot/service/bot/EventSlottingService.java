package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.feature.swap.SwapRequestService;
import de.webalf.slotbot.feature.swap.event.SwapRequestAcceptedEvent;
import de.webalf.slotbot.feature.swap.event.SwapRequestCreatedEvent;
import de.webalf.slotbot.feature.swap.event.SwapRequestDeclinedEvent;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.event.SlotUserChangedEvent;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.DirectMessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.buttons.Button;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static de.webalf.slotbot.service.GuildService.isAMB;
import static de.webalf.slotbot.service.bot.command.event.Swap.SWAP_ACCEPT;
import static de.webalf.slotbot.service.bot.command.event.Swap.SWAP_DECLINE;
import static de.webalf.slotbot.util.bot.ButtonUtils.buildButtonId;
import static de.webalf.slotbot.util.bot.DirectMessageHelper.editDmAndRemoveComponents;
import static de.webalf.slotbot.util.bot.DirectMessageHelper.sendDm;

/**
 * @author Alf
 * @since 05.08.2023
 */
@Service
@RequiredArgsConstructor
public class EventSlottingService {
	private final DirectMessageHelper directMessageHelper;
	private final MessageSource messageSource;
	private final EventBotService eventBotService;
	private final BotService botService;
	private final SwapRequestService swapRequestService;
	private final ApplicationEventPublisher eventPublisher;

	@EventListener
	@Async
	public void slottingUpdate(@NonNull SlotUserChangedEvent changedEvent) {
		final Event event = changedEvent.event();
		final Locale guildLocale = event.getOwnerGuildLocale();

		if (changedEvent.previousUserIs()) {
			directMessageHelper.sendDmToRecipient(changedEvent.previousUser(),
					messageSource.getMessage("event.unslotted", new String[]{event.getName(), EventUtils.getDateTimeInDiscordFormat(event)}, guildLocale));
		}

		if (changedEvent.currentUserIs()) {
			final User currentUser = changedEvent.currentUser();
			final Slot slot = changedEvent.slot();
			directMessageHelper.sendDmToRecipient(currentUser,
					messageSource.getMessage("event.slotted", new String[]{event.getName(), EventUtils.getDateTimeInDiscordFormat(event), Integer.toString(slot.getNumber()), slot.getName()}, guildLocale));
			final Guild ownerGuild = event.getOwnerGuild();
			if (isAMB(ownerGuild)) {
				longTimeNoSee(currentUser, ownerGuild);
			}
		}
	}

	/**
	 * Welcomes the user if he slots for the first time.
	 * If the last event slotting is more than 3 months in the past the user gets an additional message.
	 *
	 * @param user       user that slots
	 * @param ownerGuild guild the event is hosted in
	 */
	private void longTimeNoSee(@NonNull User user, @NonNull Guild ownerGuild) {
		eventBotService.findLastEventOfUser(user, ownerGuild).ifPresentOrElse(lastEvent -> {
					if (lastEvent.getDateTime().plusMonths(3).isBefore(DateUtils.now())) {
						directMessageHelper.sendDmToRecipient(user, "Über drei Monate haben wir dich nicht mehr gesehen. Schau doch gerne mal wieder öfter vorbei. Falls du einen neuen Technikcheck brauchst oder andere Fragen hast, melde dich doch bitte bei <@327385716977958913>.");
					}
				}, () ->
						directMessageHelper.sendDmToRecipient(user, "Schön dich bei Arma macht Bock begrüßen zu dürfen. Falls du vor deiner Teilnahme einen Technikcheck machen möchtest, oder sonstige Fragen hast, melde dich bitte bei <@327385716977958913>. Ansonsten wünschen wir dir viel Spaß!")
		);
	}

	@EventListener
	@Async
	public void swapRequestCreated(@NonNull SwapRequestCreatedEvent swapRequest) {
		final JDA jda = botService.getJda();
		final net.dv8tion.jda.api.entities.User foreign = jda.retrieveUserById(swapRequest.foreign().getId()).complete();
		final net.dv8tion.jda.api.entities.User requester = jda.retrieveUserById(swapRequest.requester().getId()).complete();
		final Slot requesterSlot = swapRequest.requesterSlot();
		final Slot foreignSlot = swapRequest.foreignSlot();
		final Locale locale = swapRequest.event().getOwnerGuildLocale();
		sendDm(
				foreign,
				messageSource.getMessage("event.swapRequest", new String[]{
						requester.getAsMention(),
						Integer.toString(requesterSlot.getNumber()),
						requesterSlot.getName(),
						swapRequest.event().getName(),
						Integer.toString(foreignSlot.getNumber()),
						foreignSlot.getName()
				}, locale),
				dmMessage -> swapRequestService.addMessageId(swapRequest.swapRequestId(), dmMessage.getIdLong()),
				error -> {
					sendDm(requester, messageSource.getMessage("event.swapRequest.error", new String[]{foreign.getAsMention()}, locale));
					eventPublisher.publishEvent(SwapRequestDeclinedEvent.builder()
							.swapRequestId(swapRequest.swapRequestId())
							.requesterUserId(swapRequest.requester().getId())
							.foreignUserId(swapRequest.foreign().getId())
							.locale(locale)
							.build());
				},
				Button.success(buildButtonId(SWAP_ACCEPT, Long.toString(swapRequest.swapRequestId())), messageSource.getMessage("accept", null, locale)),
				Button.danger(buildButtonId(SWAP_DECLINE, Long.toString(swapRequest.swapRequestId())), messageSource.getMessage("decline", null, locale))
		);
	}

	@EventListener
	@Async
	public void swapRequestAccepted(@NonNull SwapRequestAcceptedEvent acceptedEvent) {
		directMessageHelper.deleteDmOfRecipient(acceptedEvent.requestedUserId(), acceptedEvent.messageId());
	}

	@EventListener
	@Async
	public void swapRequestDeclined(@NonNull SwapRequestDeclinedEvent declinedEvent) {
		if (declinedEvent.messageId() != null) {
			final net.dv8tion.jda.api.entities.User requester = botService.getJda().retrieveUserById(declinedEvent.requesterUserId()).complete();
			final net.dv8tion.jda.api.entities.User foreign = botService.getJda().retrieveUserById(declinedEvent.foreignUserId()).complete();
			editDmAndRemoveComponents(foreign, declinedEvent.messageId(), messageSource.getMessage("bot.button.swap.declined.decliner", new String[]{requester.getAsMention()}, declinedEvent.locale()));
			sendDm(requester, messageSource.getMessage("bot.button.swap.declined.requester", new String[]{foreign.getAsMention()}, declinedEvent.locale()));
		}
	}
}
