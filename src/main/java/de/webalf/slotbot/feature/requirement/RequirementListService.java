package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementPostDto;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 15.11.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RequirementListService {
	private final RequirementListRepository requirementListRepository;
	private final RequirementRepository requirementRepository;
	private final GuildService guildService;

	public RequirementList find(long id) {
		return requirementListRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	List<RequirementList> findAll(long guildId) {
		return requirementListRepository.findByGuild_IdOrGuildNullOrderByName(guildId);
	}

	RequirementList createOrUpdate(long guildId, RequirementListPostDto dto) {
		final Long id = dto.id();
		final Guild guild = guildService.findExisting(guildId);
		if (id == null) {
			return requirementListRepository.save(RequirementListAssembler.fromDto(dto, guild));
		}

		final RequirementList requirementList = requirementListRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		if (!requirementList.getGuild().equals(guild)) {
			throw new ForbiddenException("Tried to update requirement list of another guild");
		}

		requirementList.setName(dto.name());

		final List<Requirement> requirements = requirementList.getRequirements();
		requirements.clear();
		final List<Requirement> updatedRequirements = new ArrayList<>();
		dto.requirements().forEach(requirementDto -> updatedRequirements.add(createOrUpdateRequirement(requirementDto, requirementList)));
		requirements.addAll(updatedRequirements);

		requirementList.setMemberAssignable(dto.memberAssignable());
		requirementList.setEnforced(dto.enforced());

		return requirementList;
	}

	/**
	 * Updates a requirement with the given values identified by its is. If no requirement with the given id exists, a new one is created.
	 *
	 * @param dto             new values
	 * @param requirementList requirement list the requirement belongs to
	 * @return updated or new requirement
	 */
	private Requirement createOrUpdateRequirement(@NonNull RequirementPostDto dto, @NonNull RequirementList requirementList) {
		final Long id = dto.id();
		final Requirement requirement;
		if (id == null) {
			requirement = Requirement.builder().requirementList(requirementList).build();
		} else {
			requirement = requirementRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		}
		requirement.setName(dto.name());
		requirement.setIcon(dto.icon());
		return requirement;
	}
}
