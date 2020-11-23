package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.ActionLogAssembler;
import de.webalf.slotbot.model.dtos.website.ActionLogDto;
import de.webalf.slotbot.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 22.11.2020
 */
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LogController {
	private final ActionLogRepository actionLogRepository;
	private final ActionLogAssembler actionLogAssembler;

	@GetMapping("/list")
	public List<ActionLogDto> getAllLogs() {
		return actionLogRepository.findAll().stream().map(actionLogAssembler::toDto).collect(Collectors.toList());
	}
}
