package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.ActionLogAssembler;
import de.webalf.slotbot.model.ActionLog;
import de.webalf.slotbot.model.dtos.website.ActionLogDto;
import de.webalf.slotbot.model.paging.Paging;
import de.webalf.slotbot.repository.ActionLogRepository;
import de.webalf.slotbot.repository.specification.ActionLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_ADMIN;

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

	@PostMapping
	@PreAuthorize(HAS_ROLE_ADMIN)
	public PagedModel<ActionLogDto> getLogs(@RequestBody Paging page,
	                                        PagedResourcesAssembler<ActionLog> pagedResourcesAssembler) {
		ActionLogSpecification logSpecification = new ActionLogSpecification(page.getFilter());
		Pageable pageable = page.getPageable();
		Page<ActionLog> logPage = actionLogRepository.findAll(logSpecification, pageable);
		return pagedResourcesAssembler.toModel(logPage, actionLogAssembler);
	}
}
