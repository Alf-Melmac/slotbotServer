package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.EventCalendarService;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.util.EventHelper;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static de.webalf.slotbot.service.GuildService.isAMB;
import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;

/**
 * @author Alf
 * @since 11.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventUpdateService {
	private final EventHelper eventHelper;
	private final BotService botService;
	private final MessageHelper messageHelper;
	private final EventNotificationService eventNotificationService;
	private final EventCalendarService eventCalendarService;
	private final SchedulerService schedulerService;
	private final MessageSource messageSource;

	public void update(@NonNull Event event) throws IllegalStateException {
		log.trace("Update");

		event.getDiscordInformation().forEach(discordInformation -> {
			final TextChannel eventChannel = botService.getJda().getTextChannelById(discordInformation.getChannel());
			if (eventChannel == null) {
				throw new IllegalStateException("Channel " + discordInformation.getChannel() + " couldn't be found.");
			}

			final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
			final Locale guildLocale = discordInformation.getGuild().getLocale();
			eventChannel.editMessageEmbedsById(discordInformation.getInfoMsg(), eventHelper.buildDetailsEmbed(eventApiDto, guildLocale)).queue();
			final List<String> slotList = eventApiDto.getSlotList(discordInformation.getGuild().getId(), messageSource.getMessage("event.slotlist.title", null, guildLocale));
			//noinspection ConstantConditions SlotList can't be null here
			eventChannel.editMessageById(discordInformation.getSlotListMsgPartOne(), ListUtils.shift(slotList)).queue();
			eventChannel.editMessageById(discordInformation.getSlotListMsgPartTwo(), spacerCharIfEmpty(ListUtils.shift(slotList))).queue();
		});
	}

	/**
	 * {@link EventNotificationService#updateNotifications(long)} for the given event
	 *
	 * @param eventId changed event
	 */
	public void updateEventNotifications(long eventId) {
		//The action here must be scheduled, because the event must be saved before.
		//Without saving the event notifications for the event can not be found, because it comes to a StackOverflow. Hibernate bug?
		schedulerService.schedule(() -> eventNotificationService.updateNotifications(eventId), 1);
	}

	public void rebuildCalendar(long eventId) {
		//The action here must be scheduled, because the event must be saved before.
		//Without saving the events for the guild can not be found, because it comes to a StackOverflow. Hibernate bug?
		schedulerService.schedule(() -> eventCalendarService.rebuildCalendars(eventId), 1);
	}

	public void informAboutSlotChange(@NonNull Event event, @NonNull Slot slot, User currentUser, User previousUser) {
		if (Objects.equals(currentUser, previousUser)) {
			return;
		}
		final Locale guildLocale = event.getOwnerGuildLocale();
		if (previousUser != null && !previousUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(previousUser,
					messageSource.getMessage("event.unslotted", new String[]{event.getName(), EventUtils.getDateTimeInDiscordFormat(event)}, guildLocale));
			EventNotificationService.removeNotifications(event, previousUser);
			eventCalendarService.rebuildCalendar(previousUser);
		} else if (currentUser != null && !currentUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(currentUser,
					messageSource.getMessage("event.slotted", new String[]{event.getName(), EventUtils.getDateTimeInDiscordFormat(event), Integer.toString(slot.getNumber()), slot.getName()}, guildLocale));
			if (isAMB(event.getOwnerGuild())) {
				longTimeNoSee(currentUser);
			}
			schedulerService.schedule(() -> eventNotificationService.createNotifications(event, currentUser), 1);
			eventCalendarService.rebuildCalendar(currentUser);
		}
	}

	/**
	 * If the last event slotting is more than 3 months in the past the user gets an additional message
	 *
	 * @param user user that slots
	 */
	private void longTimeNoSee(@NonNull User user) {
		user.getLastEventDateTime().ifPresentOrElse(lastEvent -> {
			if (lastEvent.plusMonths(3).isBefore(LocalDateTime.now())) {
				messageHelper.sendDmToRecipient(user, "Über drei Monate haben wir dich nicht mehr gesehen. Schau doch gerne mal wieder öfter vorbei. Falls du einen neuen Technikcheck brauchst oder andere Fragen hast, melde dich doch bitte bei <@327385716977958913>.");
			}
		}, () -> messageHelper.sendDmToRecipient(user, "Schön dich bei Arma macht Bock begrüßen zu dürfen. Falls du vor deiner Teilnahme einen Technikcheck machen möchtest, oder sonstige Fragen hast, melde dich bitte bei <@327385716977958913>. Ansonsten wünschen wir dir viel Spaß!"));
	}
}
