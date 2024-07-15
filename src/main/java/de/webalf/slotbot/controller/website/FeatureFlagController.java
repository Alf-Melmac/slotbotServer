package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.web.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_ROLE_EVERYONE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * @author Alf
 * @since 12.07.2024
 */
@RestController
@RequestMapping("/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {
	private final FeatureFlagService featureFlagService;

	@GetMapping
	public Set<String> getFeatureFlags() {
		return featureFlagService.findAll();
	}

	@PostMapping(consumes = TEXT_PLAIN_VALUE)
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public boolean toggleFeatureFlag(@RequestBody String featureFlag) {
		return featureFlagService.toggle(featureFlag);
	}
}
