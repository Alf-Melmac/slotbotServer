package de.webalf.slotbot.service.web;

import de.webalf.slotbot.model.FeatureFlag;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.FeatureFlagRepository;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

	public boolean toggle(@NonNull String feature) {
		final User loggedIn = userService.getLoggedIn();
		final Optional<FeatureFlag> featureFlag = featureFlagRepository.findByFeatureAndUser(feature, loggedIn);
		if (featureFlag.isPresent()) {
			featureFlagRepository.delete(featureFlag.get());
			return false;
		} else {
			featureFlagRepository.save(FeatureFlag.builder().feature(feature).user(loggedIn).build());
			return true;
		}
	}
}
