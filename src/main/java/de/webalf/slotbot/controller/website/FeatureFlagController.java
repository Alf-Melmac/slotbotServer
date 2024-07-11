package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.web.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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
}
