package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.model.Requirement;
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

	public Set<Requirement> find(Set<Long> ids) {
		return new HashSet<>(requirementRepository.findAllById(ids));
	}
}
