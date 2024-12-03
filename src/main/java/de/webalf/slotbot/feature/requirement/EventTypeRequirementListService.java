package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.EventTypeRequirementListDto;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.service.EventTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Alf
 * @since 01.12.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
class EventTypeRequirementListService {
	private final EventTypeService eventTypeService;
	private final RequirementListService requirementListService;

	List<EventTypeRequirementListDto> findAll(long guildId, long eventTypeId) {
		final Set<RequirementList> activeRequirementLists = eventTypeService.find(eventTypeId, guildId).getRequirementList();
		final List<RequirementList> availableRequirementLists = requirementListService.findAll(guildId);
		return availableRequirementLists.stream().map(requirementList -> {
			final boolean active = activeRequirementLists.contains(requirementList);
			return RequirementListAssembler.toEventTypeDto(requirementList, active);
		}).toList();
	}
}
