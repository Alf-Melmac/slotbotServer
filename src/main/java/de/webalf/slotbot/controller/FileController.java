package de.webalf.slotbot.controller;

import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSquadDto;
import de.webalf.slotbot.util.SqmParser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;

/**
 * @author Alf
 * @since 06.06.2021
 */
@RestController
@RequestMapping("/files")
@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
public class FileController {
	@PostMapping("/uploadSqm")
	public List<MinimalSquadDto> postSqmFile(@RequestParam(name = "file") MultipartFile file) {
		return SqmParser.createSlotListFromFile(file);
	}
}
