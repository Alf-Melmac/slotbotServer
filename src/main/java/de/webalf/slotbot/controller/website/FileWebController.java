package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.util.GuildUtils.Guild;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		final Resource file = fileService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/assets/img/**")
	public ResponseEntity<Resource> getImage(HttpServletRequest request) {
		final Resource img = fileService.loadImgAsResource(request.getRequestURI().replace("/assets/img", ""));
		return ResponseEntity.ok()
				.body(img);
	}

	@GetMapping("/calendar/{filename:.+}")
	public ResponseEntity<Resource> getCalendar(@PathVariable String filename) {
		final Guild guild = Guild.findById(filename);
		if (guild != null) {
			filename = guild.getDiscordGuild() + ICS_FILE_EXTENSION;
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
			File robotsTxt = new ClassPathResource("static/assets/robots.txt").getFile();
			return new String(Files.readAllBytes(robotsTxt.toPath()));
		} catch (IOException e) {
			log.error("Failed to get robots.txt", e);
			return "";
		}
	}
}
