package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static de.webalf.slotbot.util.EventCalendarUtil.getCalendarName;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Controller
@RequiredArgsConstructor
@Slf4j
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
		final Optional<Guild> guild = guildService.findByName(filename);
		if (guild.isPresent()) {
			filename = getCalendarName(guild.get().getId());
		}

		final Resource file = fileService.loadIcsAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
				.contentType(TEXT_CALENDAR_UTF8)
				.body(file);
	}

	@GetMapping("/userContent/{userId}/{filename:.+}")
	public ResponseEntity<Resource> getUserContent(@PathVariable String userId, @PathVariable String filename) throws IOException {
		final Resource file = fileService.loadUserContentAsResource(userId, filename);
		return ResponseEntity.ok()
				.cacheControl(CacheControl
						.maxAge(30, TimeUnit.DAYS)
						.cachePublic()
						.immutable()
						.staleWhileRevalidate(31, TimeUnit.DAYS))
				.contentType(MediaType.valueOf(tika.detect(file.getInputStream(), file.getFilename())))
				.body(file);
	}
}
