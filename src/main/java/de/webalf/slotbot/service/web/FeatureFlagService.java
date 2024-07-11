package de.webalf.slotbot.service.web;

import de.webalf.slotbot.repository.FeatureFlagRepository;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Alf
 * @since 11.07.2024
 */
@Service
@RequiredArgsConstructor
public class FeatureFlagService {
	private final FeatureFlagRepository featureFlagRepository;
	private final UserService userService;
	private final GuildService guildService;

	public Set<String> findAll() {
		return featureFlagRepository.findFeaturesByUserOrGuild(userService.getPotentialLoggedIn(), guildService.findCurrentNonNullGuild());
	}
}
