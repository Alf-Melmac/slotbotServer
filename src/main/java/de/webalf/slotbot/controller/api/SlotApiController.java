package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_WRITE_PERMISSION;

/**
 * @author Alf
 * @since 21.08.2020
 */
@RequestMapping(API + "/slots")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotApiController {
	private final SlotService slotService;

	@PutMapping("/swap")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	public EventApiDto putSwap(@RequestBody List<SlotDto> slots) {
		throw new ForbiddenException("Not yet implemented");
		//TODO permission check
//		return EventApiAssembler.toDto(slotService.swap(slots));
	}
}
