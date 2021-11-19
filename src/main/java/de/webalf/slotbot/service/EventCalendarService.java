package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.util.EventCalendarUtil;
import de.webalf.slotbot.util.GuildUtils.Guild;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static de.webalf.slotbot.util.EventCalendarUtil.getCalendarName;

/**
 * @author Alf
 * @since 06.08.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventCalendarService {
	private final StorageProperties storageProperties;

	/**
	 * Rebuilds the calendar for the given guild. This calendar includes all public visible events that the guild hosts or participates in.
	 *
	 * @param guild  to rebuild calendar for
	 * @param events to include in the calendar
	 */
	//Parameter events should be removed once guild is a database entity and knows it events
	public void rebuildCalendar(@NonNull Guild guild, Iterable<Event> events) {
		log.debug("Building calendar for guild {} [{}]", guild.getId(), guild.getDiscordGuild());
		final Calendar calendar = EventCalendarUtil.buildEventCalendar(events);
		writeCalendar(calendar, getCalendarPath(guild));
		log.trace("Calendar write finished.");
	}

	/**
	 * Rebuilds the calendar for the given user if {@link User#isExternalCalendarIntegrationActive()}.
	 * Otherwise, any existing calendar gets deleted
	 *
	 * @param user to rebuild calendar for
	 */
	public void rebuildCalendar(@NonNull User user) {
		if (!user.isExternalCalendarIntegrationActive()) {
			deleteCalendar(user);
			return;
		}

		log.debug("Building calendar for {}", user.getId());
		final List<Event> events = user.getSlottedEvents();
		if (events.isEmpty()) {
			deleteCalendar(user);
			return;
		}
		final Calendar calendar = EventCalendarUtil.buildEventCalendar(events);
		writeCalendar(calendar, getCalendarPath(user));
		log.trace("Calendar write finished.");
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
	 * Deletes the calendar for the given user
	 *
	 * @param user to delete calendar for
	 */
	private void deleteCalendar(User user) {
		try {
			log.debug("Deleting calendar of {}", user.getId());
			Files.deleteIfExists(Paths.get(getCalendarPath(user)));
			log.trace("Deleted calendar.");
		} catch (IOException e) {
			log.error("Failed to delete calendar of {}", user.getId(), e);
		}
	}

	private String getCalendarPath(Guild guild) {
		return getCalendarPath(getCalendarName(guild));
	}

	private String getCalendarPath(User user) {
		return getCalendarPath(getCalendarName(user));
	}

	private String getCalendarPath(@NotBlank String calendarName) {
		return storageProperties.getCalendarOutput() + "/" + calendarName;
	}
}
