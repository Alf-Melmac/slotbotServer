package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.event.SlotUserChangedEvent;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static de.webalf.slotbot.service.GuildService.isAMB;

/**
 * @author Alf
 * @since 05.08.2023
 */
@Service
@RequiredArgsConstructor
public class EventSlottingService {
	private final MessageHelper messageHelper;
	private final MessageSource messageSource;
	private final EventBotService eventBotService;

	@EventListener
	@Async
	public void slottingUpdate(@NonNull SlotUserChangedEvent changedEvent) {
		final Event event = changedEvent.event();
		final Locale guildLocale = event.getOwnerGuildLocale();

		if (changedEvent.previousUserIs()) {
			messageHelper.sendDmToRecipient(changedEvent.previousUser(),
					messageSource.getMessage("event.unslotted", new String[]{event.getName(), EventUtils.getDateTimeInDiscordFormat(event)}, guildLocale));
		}

		if (changedEvent.currentUserIs()) {
			final User currentUser = changedEvent.currentUser();
			final Slot slot = changedEvent.slot();
			messageHelper.sendDmToRecipient(currentUser,
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
						messageHelper.sendDmToRecipient(user, "Über drei Monate haben wir dich nicht mehr gesehen. Schau doch gerne mal wieder öfter vorbei. Falls du einen neuen Technikcheck brauchst oder andere Fragen hast, melde dich doch bitte bei <@327385716977958913>.");
					}
				}, () ->
				messageHelper.sendDmToRecipient(user, "Schön dich bei Arma macht Bock begrüßen zu dürfen. Falls du vor deiner Teilnahme einen Technikcheck machen möchtest, oder sonstige Fragen hast, melde dich bitte bei <@327385716977958913>. Ansonsten wünschen wir dir viel Spaß!")
		);
	}
}
