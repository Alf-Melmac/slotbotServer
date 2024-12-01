package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.feature.requirement.dto.EventTypeRequirementListDto;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.EventType;
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
public class EventTypeRequirementListService {
	private final EventTypeService eventTypeService;
	private final RequirementListService requirementListService;

	public List<EventTypeRequirementListDto> findAll(long guildId, long eventTypeId) {
		final EventType eventType = eventTypeService.find(eventTypeId);
		if (eventType.getGuild().getId() != guildId) {
			throw new ForbiddenException("Event type does not belong to this guild");
		}
		final Set<RequirementList> activeRequirementLists = eventType.getRequirementList();
		final List<RequirementList> availableRequirementLists = requirementListService.findAll(guildId);
		return availableRequirementLists.stream().map(requirementList -> {
			final boolean active = activeRequirementLists.contains(requirementList);
			return RequirementListAssembler.toEventTypeDto(requirementList, active);
		}).toList();
	}
}
