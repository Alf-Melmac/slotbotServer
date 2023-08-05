package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.event.EventMetadataUpdateEvent;
import de.webalf.slotbot.model.event.SlotUserChangedEvent;
import de.webalf.slotbot.util.EventCalendarUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Alf
 * @since 06.08.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventCalendarService {
	private final StorageProperties storageProperties;
	private final EventService eventService;

	/**
	 * Rebuilds the calendar for the given guild. This calendar includes all public visible events that the guild hosts or participates in.
	 *
	 * @param guild to rebuild calendar for
	 */
	public void rebuildCalendar(@NonNull Guild guild) {
		if (!guild.isAdvanced()) {
			return;
		}

		log.trace("Building calendar for guild {} [{}]", guild.getGroupIdentifier(), guild.getId());
		//This may be moved to the guild entity once shared events are persistently matched to foreign guilds
		final List<Event> events = eventService.findAllPublicByGuild(guild);
		buildAndWrite(events, guild.getId());
	}

	@EventListener
	@Async
	public void rebuildCalendar(@NonNull SlotUserChangedEvent changedEvent) {
		if (changedEvent.previousUserIs()) {
			rebuildCalendar(changedEvent.previousUser());
		}

		if (changedEvent.currentUserIs()) {
			rebuildCalendar(changedEvent.currentUser());
		}
	}

	/**
	 * Rebuilds the calendar for the given user if {@link User#isExternalCalendarIntegrationActive()}.
	 * Otherwise, any existing calendar gets deleted
	 *
	 * @param user to rebuild calendar for
	 */
	public void rebuildCalendar(@NonNull User user) {
		if (user.isDefaultUser()) {
			return;
		}
		if (!user.isExternalCalendarIntegrationActive()) {
			deleteCalendar(user.getId());
			return;
		}
		log.trace("Building calendar for user {}", user.getId());
		buildAndWrite(eventService.findEventsOfUser(user), user.getId());
	}

	/**
	 * Rebuilds the calendar for the given event
	 */
	@EventListener
	@Async
	public void rebuildCalendars(@NonNull EventMetadataUpdateEvent updateEvent) {
		final Event event = eventService.findById(updateEvent.eventId());
		log.trace("Creating calendars for event {} [{}]", event.getName(), event.getId());
		event.getAllParticipants().forEach(this::rebuildCalendar);
		Stream.concat(Stream.of(event.getOwnerGuild()), event.getDiscordInformation().stream().map(EventDiscordInformation::getGuild))
				.distinct()
				.forEach(this::rebuildCalendar);
	}

	private void buildAndWrite(@NonNull List<Event> events, long id) {
		if (events.isEmpty()) {
			deleteCalendar(id);
			return;
		}

		log.debug("Building calendar for {}", id);
		final Calendar calendar = EventCalendarUtil.buildEventCalendar(events);
		writeCalendar(calendar, getCalendarPath(id));
		log.trace("Calendar write finished. {} events written", events.size());
	}

	/**
	 * Writes the given calendar as an ics file to the given path
	 *
	 * @param calendar     to write
	 * @param calendarPath path to write calendar to
	 */
	private void writeCalendar(Calendar calendar, @NotBlank String calendarPath) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(calendarPath)) {
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(calendar, fileOutputStream);
		} catch (FileNotFoundException e) {
			log.error("Failed to write calendar file", e);
		} catch (IOException e) {
			log.error("Failed to output calendar", e);
		}
	}

	/**
	 * Deletes the calendar for the given id
	 *
	 * @param id to delete calendar for. Can be a user or a guild
	 */
	private void deleteCalendar(long id) {
		try {
			log.trace("Deleting calendar {}", id);
			final boolean deleted = Files.deleteIfExists(Paths.get(getCalendarPath(id)));
			if (deleted && log.isDebugEnabled()) {
				log.debug("Deleted calendar of {}", id);
			}
		} catch (IOException e) {
			log.error("Failed to delete calendar of {}", id, e);
		}
	}

	private String getCalendarPath(long id) {
		return storageProperties.getCalendarOutput() + "/" + EventCalendarUtil.getCalendarName(id);
	}
}
