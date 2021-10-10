package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static de.webalf.slotbot.util.DateUtils.DATE_FORMATTER;
import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;

/**
 * @author Alf
 * @since 11.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventUpdateService {
	private final BotService botService;
	private final MessageHelper messageHelper;
	private final EventNotificationService eventNotificationService;
	private final SchedulerService schedulerService;

	public void update(@NonNull Event event) throws IllegalStateException {
		log.trace("Update");

		event.getDiscordInformation().forEach(discordInformation -> {
			final TextChannel eventChannel = botService.getJda().getTextChannelById(discordInformation.getChannel());
			if (eventChannel == null) {
				throw new IllegalStateException("Channel " + discordInformation.getChannel() + " couldn't be found.");
			}

			final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
			eventChannel.editMessageEmbedsById(discordInformation.getInfoMsg(), EventUtils.buildDetailsEmbed(eventApiDto)).queue();
			final List<String> slotList = eventApiDto.getSlotList();
			//noinspection ConstantConditions SlotList can't be null here
			eventChannel.editMessageById(discordInformation.getSlotListMsgPartOne(), ListUtils.shift(slotList)).queue();
			eventChannel.editMessageById(discordInformation.getSlotListMsgPartTwo(), spacerCharIfEmpty(ListUtils.shift(slotList))).queue();
		});
	}

	/**
	 * Checks if the event date has changed and {@link EventNotificationService#updateNotifications(long)} if needed
	 *
	 * @param oldEventDateTime old event time
	 * @param newEventDateTime edited event time
	 * @param eventId          changed event
	 */
	public void updateEventNotifications(@NonNull LocalDateTime oldEventDateTime, LocalDateTime newEventDateTime, long eventId) {
		if (!oldEventDateTime.isEqual(newEventDateTime)) {
			//The action here must be scheduled, because the event must be saved before.
			//Without saving the event notifications for the event can not be found, because it comes to a StackOverflow. Hibernate bug?
			schedulerService.schedule(() -> eventNotificationService.updateNotifications(eventId), 1);
		}
	}

	public void informAboutSlotChange(@NonNull Event event, @NonNull Slot slot, User currentUser, User previousUser) {
		final String eventDate = DATE_FORMATTER.format(event.getDateTime().toLocalDate());
		if (previousUser != null && !previousUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(previousUser, "Du bist nun vom Event **" + event.getName() + "** am " + eventDate + " ausgetragen.");
			EventNotificationService.removeNotifications(event, previousUser);
		} else if (currentUser != null && !currentUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(currentUser, "Du bist im Event **" + event.getName() + "** am " + eventDate + " nun auf dem Slot " + slot.getNumber() + " *" + slot.getName() + "* eingetragen.");
			longTimeNoSee(currentUser);
			schedulerService.schedule(() -> eventNotificationService.createNotifications(event, currentUser), 1);
		}
	}

	/**
	 * If the last event slotting is more than 3 months in the past the user gets an additional message
	 *
	 * @param currentUser user that slots
	 */
	private void longTimeNoSee(@NonNull User currentUser) {
		currentUser.getLastEventDateTime().ifPresent(lastEvent -> {
			if (lastEvent.plusMonths(3).isBefore(LocalDateTime.now())) {
				messageHelper.sendDmToRecipient(currentUser, "Über drei Monate haben wir dich nicht mehr gesehen. Schau doch gerne mal wieder öfter vorbei. Falls du einen neuen Technikcheck brauchst oder andere Fragen hast, melde dich doch bitte bei <@327385716977958913>.");
			}
		});
	}
}
