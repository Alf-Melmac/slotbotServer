package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Alf
 * @since 05.08.2021
 */
@UtilityClass
@Slf4j
public final class EventCalendarUtil {
	public static Calendar buildEventCalendar(@NonNull List<Event> eventList) {
		final ComponentList<CalendarComponent> calendarComponents = new ComponentList<>(
				eventList.stream()
						.map(EventCalendarUtil::buildVEvent)
						.toList()
		);

		return new Calendar(calendarComponents)
				.withProdId("-//Alf//Slotbot//DE")
				.withProperty(new Name("Slotbot Calendar"))
				.withDefaults()
				.getFluentTarget();
	}

	private static VEvent buildVEvent(@NonNull Event event) {
		final String eventName = event.getName();
		final ZonedDateTime eventDateTime = DateUtils.getDateTimeZoned(event.getDateTime());

		final VEvent vEvent = new VEvent(eventDateTime, eventName);
		final String eventUrl = EventUtils.buildUrl(event);
		try {
			vEvent.add(new Url(new URI(eventUrl)));
		} catch (URISyntaxException _) {
			log.error("Event ({}) url '{}' isn't valid. Can't add to calendar", event.getId(), eventUrl);
		}

		final UUID id = UUID.nameUUIDFromBytes(ByteBuffer
				.allocate(Long.BYTES)
				.putLong(event.getId())
				.array());
		vEvent.add(new Uid(id.toString()));

		return vEvent;
	}

	private static final String ICS_FILE_EXTENSION = ".ics";

	public static String getCalendarName(long id) {
		return id + ICS_FILE_EXTENSION;
	}
}
