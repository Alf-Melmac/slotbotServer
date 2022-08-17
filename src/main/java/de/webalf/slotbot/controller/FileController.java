package de.webalf.slotbot.controller;

import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSquadDto;
import de.webalf.slotbot.util.SqmParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Alf
 * @since 06.06.2021
 */
@RestController
@RequestMapping("/files")
public class FileController {
	@PostMapping("/uploadSqm")
	public List<MinimalSquadDto> postSqmFile(@RequestParam(name = "file") MultipartFile file) {
		return SqmParser.createSlotListFromFile(file);
	}
}
