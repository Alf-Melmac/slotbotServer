package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.exception.ResourceNotFoundException;
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

	public Requirement find(long id) {
		return requirementRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	public Set<Requirement> find(Set<Long> ids) {
		return new HashSet<>(requirementRepository.findAllById(ids));
	}
}
