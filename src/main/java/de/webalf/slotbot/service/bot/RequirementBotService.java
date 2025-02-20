package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.feature.requirement.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for {@link RequirementService} to be used by discord bot
 *
 * @author Alf
 * @since 20.02.2025
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RequirementBotService {
	private final RequirementService requirementService;

	public void fulfillRequirement(long requirementId, long userId) {
		requirementService.fulfillRequirement(requirementId, userId, true);
	}
}
