package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.NotificationMap;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.utils.TimeFormat.RELATIVE;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventNotificationService {
	private final SchedulerService schedulerService;
	private final NotificationSettingsService notificationSettingsService;
	private final MessageHelper messageHelper;
	private final EventService eventService;
	private final MessageSource messageSource;

	@Getter
	private static final Map<NotificationIdentifier, ScheduledFuture<?>> SCHEDULED_NOTIFICATIONS = new NotificationMap<>();

	@Value
	@Builder
	@EqualsAndHashCode
	public static class NotificationIdentifier {
		long eventId;
		long userId;
		int delay;
	}

	/**
	 * Creates a new notification for given user in given event
	 *
	 * @param event about which notification is to be sent
	 * @param user  to notify
	 */
	public void createNotifications(@NonNull Event event, User user) {
		final Locale guildLocale = event.getOwnerGuildLocale();

		notificationSettingsService.findSettings(user, event).forEach(notificationSetting -> {
			final int delay = notificationSetting.getNotificationDelay(event.getDateTime());
			if (delay < 0) {
				return;
			}
			SCHEDULED_NOTIFICATIONS.computeIfAbsent(buildNotificationIdentifier(event, user, delay),
					k -> schedulerService.schedule(() -> {
						messageHelper.sendDmToRecipient(user, messageSource.getMessage("event.reminder", new String[]{event.getName(), RELATIVE.format(event.getDateTime())}, guildLocale));
						SCHEDULED_NOTIFICATIONS.remove(k);
					}, delay));
		});
	}

	/**
	 * Deletes all existing and reschedules event notifications
	 */
	public void rebuildAllNotifications() {
		SCHEDULED_NOTIFICATIONS.clear();
		final List<Event> allInFuture = eventService.findAllInFuture();
		log.info("Building notifications for {} events.", allInFuture.size());
		allInFuture.forEach(this::createNotificationsForAllParticipants);
	}

	/**
	 * Recreates all notifications for the given event
	 *
	 * @param eventId event to refresh notifications for
	 */
	public void updateNotifications(long eventId) {
		removeNotifications(eventId);
		createNotificationsForAllParticipants(eventService.findById(eventId));
	}

	/**
	 * {@link #createNotifications(Event, User)} for every participant of the given event ({@link Event#getAllParticipants()})
	 *
	 * @param event to create notifications for
	 */
	private void createNotificationsForAllParticipants(@NonNull Event event) {
		event.getAllParticipants().forEach(user ->
				createNotifications(event, user));
	}

	/**
	 * Removes all notifications for the given event and user combination
	 *
	 * @param event about which notifications should no longer be sent
	 * @param user  to notify
	 */
	public static void removeNotifications(@NonNull Event event, @NonNull User user) {
		getNotificationIdentifiersWhere(identifier -> identifier.getUserId() == user.getId() && identifier.getEventId() == event.getId())
				.forEach(SCHEDULED_NOTIFICATIONS::remove);
	}

	/**
	 * Removes all notifications for the given event
	 *
	 * @param eventId event to no longer notify about
	 */
	public static void removeNotifications(long eventId) {
		getNotificationIdentifiersWhere(identifier -> identifier.getEventId() == eventId)
				.forEach(SCHEDULED_NOTIFICATIONS::remove);
	}

	private static NotificationIdentifier buildNotificationIdentifier(@NonNull Event event, @NonNull User user, int delay) {
		return NotificationIdentifier.builder()
				.eventId(event.getId())
				.userId(user.getId())
				.delay(delay)
				.build();
	}

	private static Set<NotificationIdentifier> getNotificationIdentifiersWhere(Predicate<NotificationIdentifier> filterPredicate) {
		return SCHEDULED_NOTIFICATIONS.keySet().stream().filter(filterPredicate).collect(Collectors.toUnmodifiableSet());
	}
}
