package de.webalf.slotbot.feature.requirement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_ROLE_EVERYONE;

/**
 * @author Alf
 * @since 10.02.2025
 */
@RestController
@RequestMapping("/requirements")
@RequiredArgsConstructor
@Slf4j
public class RequirementController {
	private final RequirementService requirementService;

	@PutMapping("/{requirementId}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public void fulfillRequirement(@PathVariable(name = "requirementId") long requirementId) {
		requirementService.fulfillRequirement(requirementId);
	}

	@PutMapping("/{requirementId}/guild/{guildId}/user/{userId}/{fulfilled}")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public void fulfillRequirementForUser(@PathVariable(name = "requirementId") long requirementId,
	                                      @SuppressWarnings("unused") //Used for permission check
	                                      @PathVariable(name = "guildId") long guildId,
	                                      @PathVariable(name = "userId") long userId,
	                                      @PathVariable(name = "fulfilled") boolean fulfilled) {
		requirementService.fulfillRequirement(requirementId, userId, fulfilled);
	}
}
