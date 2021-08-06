package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.EventCalendarUtil;
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
	private final EventRepository eventRepository;

	/**
	 * Builds and writes the calendar including all visible events
	 */
	public void rebuildCalendar() {
		log.debug("Rebuilding calendar...");
		final Calendar calendar = EventCalendarUtil.buildEventCalendar(eventRepository.findAllByHiddenFalse());
		writeCalendar(calendar, "event-all");
		log.debug("Calendar write finished.");
	}

	/**
	 * Writes the given calendar as an ics file to the {@link StorageProperties#calendarOutput} directory
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
