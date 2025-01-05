package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.EventTypeRequirementListDto;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.GuildService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
	private final GuildService guildService;

	/**
	 * Returns all available requirement lists for the given event type
	 *
	 * @param guildId     of the event type
	 * @param eventTypeId to find requirement lists for
	 * @return available requirement lists
	 */
	List<EventTypeRequirementListDto> findAll(long guildId, long eventTypeId) {
		final List<RequirementList> activeRequirementLists = findAllActive(guildId, eventTypeId);
		final List<RequirementList> availableRequirementLists = requirementListService.findAll(guildId);
		return availableRequirementLists.stream().map(requirementList -> {
			final boolean active = activeRequirementLists.contains(requirementList);
			return EventTypeRequirementListAssembler.toDto(requirementList, active);
		}).toList();
	}

	/**
	 * Returns all enabled requirement lists for the given event type
	 *
	 * @param guildId     of the event type
	 * @param eventTypeId to find requirement lists for
	 * @return all enabled requirement lists
	 */
	List<RequirementList> findAllActive(long guildId, long eventTypeId) {
		return eventTypeService.find(eventTypeId, guildId).getRequirementList();
	}

	/**
	 * @see #findAllActive(long, long)
	 */
	List<RequirementList> findAllActive(String guild, long eventTypeId) {
		return findAllActive(guildService.findByIdentifier(guild).getId(), eventTypeId);
	}
}
