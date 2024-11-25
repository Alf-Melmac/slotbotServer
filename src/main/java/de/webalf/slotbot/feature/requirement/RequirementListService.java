package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alf
 * @since 15.11.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
class RequirementListService {
	private final RequirementListRepository requirementListRepository;
	private final RequirementListAssembler requirementListAssembler;

	List<RequirementList> findAll(long guildId) {
		return requirementListRepository.findByGuild_IdOrGuildNull(guildId);
	}

	RequirementList create(long guildId, RequirementListPostDto requirementList) {
		return requirementListRepository.save(requirementListAssembler.fromDto(requirementList, guildId));
	}
}
