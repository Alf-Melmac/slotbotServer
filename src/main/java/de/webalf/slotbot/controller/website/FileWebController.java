package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Optional;

import static de.webalf.slotbot.util.EventCalendarUtil.ICS_FILE_EXTENSION;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class FileWebController {
	private final FileService fileService;
	private final GuildService guildService;

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
			filename = guild.get().getId() + ICS_FILE_EXTENSION;
		}

		final Resource file = fileService.loadIcsAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(file);
	}

	@GetMapping(value = "/robots.txt", produces = TEXT_PLAIN_VALUE)
	@ResponseBody
	public String getRobotsTxt() {
		try {
			return new String(new ClassPathResource("static/assets/robots.txt").getInputStream().readAllBytes());
		} catch (IOException e) {
			log.error("Failed to get robots.txt", e);
			return "";
		}
	}
}
