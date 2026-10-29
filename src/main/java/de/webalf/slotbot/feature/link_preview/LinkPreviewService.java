package de.webalf.slotbot.feature.link_preview;

import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.web.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

/**
 * @author Alf
 * @since 26.10.2026
 */
@Service
@RequiredArgsConstructor
public class LinkPreviewService {
	private final EventService eventService;
	private final RedirectService redirectService;

	private static final ZoneId EVENT_ZONE = ZoneId.of("Europe/Berlin");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy, HH:mm 'Uhr'", Locale.GERMAN);
	private static final String TEMPLATE = """
			<!DOCTYPE html>
			<html lang="de"><head>
			  <meta charset="UTF-8"/>
			  <title>%1$s</title>
			  <meta property="og:site_name" content="Slotbot"/>
			  <meta property="og:type" content="website"/>
			  <meta property="og:url" content="%2$s"/>
			  <meta property="og:title" content="%1$s"/>
			  <meta property="og:description" content="%3$s"/>
			  <meta property="og:image" content="%4$s"/>
			  <meta name="theme-color" content="#00FFFF"/>
			  <meta name="twitter:card" content="summary"/>
			</head><body></body></html>
			""";

	/**
	 * Builds Open Graph html for the given event. Hidden or unknown events get the same generic preview
	 */
	public String buildEventPreview(String eventId) {
		return parseId(eventId)
				.flatMap(eventService::findOptionalById)
				.filter(event -> !event.isHidden())
				.map(event -> toHtml(
						event.getName(),
						redirectService.redirectTo("/events/" + event.getId()),
						DATE_TIME_FORMATTER.format(event.getDateTimeAtUtcOffset().atZoneSameInstant(EVENT_ZONE)) + " · " + event.getEventType().getName(),
						event.getPictureUrlWithDefault()))
				.orElseGet(() -> toHtml("Slotbot", redirectService.redirectTo("/"), "", redirectService.redirectTo("/slotbot-256-256.png")));
	}

	private static String toHtml(String title, String url, String description, String imageUrl) {
		return TEMPLATE.formatted(escape(title), escape(url), escape(description), escape(imageUrl));
	}

	private static String escape(String s) {
		return htmlEscape(s, StandardCharsets.UTF_8.name());
	}

	private static Optional<Long> parseId(String id) {
		try {
			return Optional.of(Long.parseLong(id));
		} catch (NumberFormatException _) {
			return Optional.empty();
		}
	}
}
