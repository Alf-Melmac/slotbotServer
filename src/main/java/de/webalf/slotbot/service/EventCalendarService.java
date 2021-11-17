package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.util.EventCalendarUtil;
import de.webalf.slotbot.util.GuildUtils.Guild;
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
	private final EventService eventService;

	/**
	 * Builds and writes the calendar including all visible events
	 */
	public void rebuildCalendars() {
		log.debug("Rebuilding global calendars...");
		for (Guild guild : Guild.values()) {
			log.debug("Building calendar for {} [{}]", guild.getId(), guild.getDiscordGuild());
			final Calendar calendar = EventCalendarUtil.buildEventCalendar(eventService.findAllByGuild(guild.getDiscordGuild()));
			writeCalendar(calendar, guild.getId());
		}
		log.debug("Calendar writes finished.");
	}

	/**
	 * Writes the given calendar as an ics file to the {@link StorageProperties#getCalendarOutput()} directory
	 *
	 * @param calendar to write
	 * @param calendarName file name
	 */
	private void writeCalendar(Calendar calendar, @NotBlank String calendarName) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(storageProperties.getCalendarOutput() + "/" + calendarName + ".ics")) {
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(calendar, fileOutputStream);
		} catch (FileNotFoundException e) {
			log.error("Failed to write calendar file", e);
		} catch (IOException e) {
			log.error("Failed to output calendar", e);
		}
	}
}
