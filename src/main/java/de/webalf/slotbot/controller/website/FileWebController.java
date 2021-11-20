package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.util.GuildUtils.Guild;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

import static de.webalf.slotbot.util.EventCalendarUtil.ICS_FILE_EXTENSION;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
}
