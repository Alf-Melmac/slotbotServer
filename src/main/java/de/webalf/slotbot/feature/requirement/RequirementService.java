package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alf
 * @since 05.01.2025
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RequirementService {
	private final RequirementRepository requirementRepository;
	private final UserService userService;

	public Requirement find(long id) {
		return requirementRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	public Set<Requirement> find(Set<Long> ids) {
		return new HashSet<>(requirementRepository.findAllById(ids));
	}

	/**
	 * Fulfills the requirement with the given id for the currently logged-in user
	 *
	 * @throws ForbiddenException if the requirement is not member assignable
	 */
	public void fulfillRequirement(long requirementId) {
		final Requirement requirement = find(requirementId);
		if (!requirement.isMemberAssignable()) {
			throw new ForbiddenException("Requirement is not member assignable");
		}
		final User user = userService.getLoggedIn();
		user.addFulfilledRequirement(requirement);
	}

	/**
	 * Fulfills the given requirement for the given user if fulfilled is true, removes it otherwise
	 *
	 * @param requirementId to fulfill
	 * @param userId        to fulfill requirement for
	 * @param fulfilled     new requirement fulfillment state
	 */
	public void fulfillRequirement(long requirementId, long userId, boolean fulfilled) {
		final Requirement requirement = find(requirementId);
		final User user = userService.findExisting(userId);
		if (fulfilled) {
			user.addFulfilledRequirement(requirement);
		} else {
			user.removeFulfilledRequirement(requirement);
		}
	}
}
