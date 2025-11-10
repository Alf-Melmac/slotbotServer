package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 05.08.2021
 */
@UtilityClass
@Slf4j
public final class EventCalendarUtil {
	private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();

	public static Calendar buildEventCalendar(@NonNull Iterable<Event> eventList) {
		// Create a calendar
		Calendar icsCalendar = new Calendar();
		icsCalendar.add(new ProdId("-//Alf//Slotbot Calendar//DE"));
		icsCalendar.add(ImmutableVersion.VERSION_2_0);
		icsCalendar.add(ImmutableCalScale.GREGORIAN);
		icsCalendar.add(new Name("Slotbot Kalender"));

		eventList.forEach(event -> addCalendarEvent(icsCalendar, event));

		return icsCalendar;
	}

	private static void addCalendarEvent(@NonNull Calendar calendar, @NonNull Event event) {
		final String eventName = event.getName();
		final LocalDateTime eventDateTime = event.getDateTime();

		// Create the event
		VEvent calendarEvent = new VEvent(DateUtils.getDateTimeZoned(eventDateTime), eventName);
		final String eventUrl = EventUtils.buildUrl(event);
		try {
			calendarEvent.add(new Url(new URI(eventUrl)));
		} catch (URISyntaxException _) {
			log.error("Event url '{}' isn't valid. Can't add to calendar event {}", eventUrl, eventName);
		}

		// generate unique identifier..
		calendarEvent.add(UID_GENERATOR.generateUid());

		// Add the event
		calendar.add(calendarEvent);
	}

	public static final String ICS_FILE_EXTENSION = ".ics";

	public static String getCalendarName(long id) {
		return id + ICS_FILE_EXTENSION;
	}
}
