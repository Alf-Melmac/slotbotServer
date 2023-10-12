package de.webalf.slotbot.controller;

import de.webalf.slotbot.controller.website.FileWebController;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSquadDto;
import de.webalf.slotbot.service.ImageService;
import de.webalf.slotbot.util.SqmParser;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 06.06.2021
 */
@RestController
@RequestMapping("/files")
@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
@RequiredArgsConstructor
public class FileController {
	private final ImageService imageService;

	@PostMapping(value = "/uploadImage", produces = MediaType.TEXT_PLAIN_VALUE)
	public String postImageFile(@RequestParam(name = "file") MultipartFile file) throws IOException {
		final String imageFileName = imageService.writeImage(file);
		return linkTo(methodOn(FileWebController.class).getUserContent(PermissionHelper.getLoggedInUserId(), imageFileName)).toUri().toString();
	}

	@PostMapping("/uploadSqm")
	public List<MinimalSquadDto> postSqmFile(@RequestParam(name = "file") MultipartFile file) {
		return SqmParser.createSlotListFromFile(file);
	}
}
