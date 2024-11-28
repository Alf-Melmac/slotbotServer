package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Alf
 * @since 15.11.2024
 */
@RestController
@RequestMapping("/requirement-list")
@RequiredArgsConstructor
public class RequirementListController {
	private final RequirementListService requirementListService;

	@GetMapping("/{guildId}")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public List<RequirementListDto> getRequirementLists(@PathVariable(value = "guildId") long guildId) {
		return RequirementListAssembler.toDtoList(requirementListService.findAll(guildId));
	}

	@PutMapping("/{guildId}")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public RequirementListDto putRequirementList(@PathVariable(value = "guildId") long guildId, @RequestBody RequirementListPostDto requirementList) {
		return RequirementListAssembler.toDto(requirementListService.createOrUpdate(guildId, requirementList));
	}
}
