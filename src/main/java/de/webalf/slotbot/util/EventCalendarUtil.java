package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

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
		icsCalendar.add(Version.VERSION_2_0);
		icsCalendar.add(CalScale.GREGORIAN);
		icsCalendar.add(new Name("Slotbot Kalender"));

		eventList.forEach(event -> addCalendarEvent(icsCalendar, event));

		return icsCalendar;
	}

	private static void addCalendarEvent(@NonNull Calendar calendar, @NonNull Event event) {
		final String eventName = event.getName();
		final ZonedDateTime eventDateTime = DateUtils.getDateTimeZoned(event.getDateTime());

		// Create the event
		VEvent calendarEvent = new VEvent(eventDateTime, eventName);
		final String eventUrl = EventUtils.buildUrl(event.getId());
		try {
			calendarEvent.add(new Url(new URI(eventUrl)));
		} catch (URISyntaxException e) {
			log.error("Event url '{}' isn't valid. Can't add to calendar event {}", eventUrl, eventName);
		}

		// generate unique identifier..
		calendarEvent.add(UID_GENERATOR.generateUid());

		// Add the event
		calendar.add(calendarEvent);
	}
}
