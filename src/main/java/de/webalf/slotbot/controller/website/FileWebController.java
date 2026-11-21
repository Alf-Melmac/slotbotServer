package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static de.webalf.slotbot.util.EventCalendarUtil.getCalendarName;
import static de.webalf.slotbot.util.EventCalendarUtil.isCalendarName;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Controller
@RequiredArgsConstructor
public class FileWebController {
	private final FileService fileService;
	private final GuildService guildService;

	private final Tika tika = new Tika();

	private static final MediaType TEXT_CALENDAR_UTF8 = new MediaType("text", "calendar", StandardCharsets.UTF_8);

	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		final Resource file = fileService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/calendar/{filename:.+}")
	public ResponseEntity<Resource> getCalendar(@PathVariable String filename) {
		final String calendarName = isCalendarName(filename) ? filename : guildService.findByName(filename)
				.map(guild -> getCalendarName(guild.getId()))
				.orElse(filename);

		final Resource file = fileService.loadIcsAsResource(calendarName);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
				.contentType(TEXT_CALENDAR_UTF8)
				.body(file);
	}

	@GetMapping("/userContent/{userId}/{filename:.+}")
	public ResponseEntity<Resource> getUserContent(@PathVariable String userId, @PathVariable String filename) throws IOException {
		final Resource file = fileService.loadUserContentAsResource(userId, filename);
		final MediaType mediaType;
		try (final InputStream inputStream = file.getInputStream()) {
			mediaType = MediaType.valueOf(tika.detect(inputStream, file.getFilename()));
		}
		return ResponseEntity.ok()
				.cacheControl(CacheControl
						.maxAge(30, TimeUnit.DAYS)
						.cachePublic()
						.immutable()
						.staleWhileRevalidate(31, TimeUnit.DAYS))
				.contentType(mediaType)
				.body(file);
	}
}
