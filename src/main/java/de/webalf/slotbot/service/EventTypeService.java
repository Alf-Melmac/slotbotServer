package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.api.event.EventTypeApiAssembler;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.feature.requirement.RequirementListService;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.model.dtos.api.event.EventTypeApiDto;
import de.webalf.slotbot.repository.EventTypeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventTypeService {
	private final EventTypeRepository eventTypeRepository;
	private final GuildService guildService;
	private final RequirementListService requirementListService;

	/**
	 * Finds a {@link EventType} by id and validates that it belongs to the given guild
	 *
	 * @param id      event type id to find
	 * @param guildId guild the event type should belong to
	 * @return found event type
	 */
	public EventType find(long id, long guildId) {
		final EventType eventType = eventTypeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		if (eventType.getGuild().getId() != guildId) {
			throw new ForbiddenException("Event type does not belong to this guild");
		}
		return eventType;
	}

	/**
	 * Finds a {@link EventType} by name and color of given dto or creates a new one with values from given dto
	 *
	 * @param eventTypeDto to find
	 * @param guild        to find event type for
	 * @return found eventType or new eventType
	 */
	public EventType find(@NonNull EventTypeDto eventTypeDto, @NonNull Guild guild) {
		return eventTypeRepository.findByNameAndColorAndGuild(eventTypeDto.getName(), eventTypeDto.getColor(), guild)
				.orElseGet(() -> eventTypeRepository.save(EventTypeAssembler.fromDto(eventTypeDto, guild)));
	}

	/**
	 * Finds a {@link EventType} by name and color of given dto or creates a new one with values from given dto
	 *
	 * @param eventTypeDto to find
	 * @param guild        to find event type for
	 * @return found eventType or new eventType
	 */
	public EventType find(@NonNull EventTypeApiDto eventTypeDto, Guild guild) {
		return eventTypeRepository.findByNameAndColorAndGuild(eventTypeDto.getName(), eventTypeDto.getColor(), guild)
				.orElseGet(() -> eventTypeRepository.save(EventTypeApiAssembler.fromDto(eventTypeDto, guild)));
	}

	/**
	 * @see #findAllOrdered(Guild)
	 */
	public List<EventType> findAllOrdered(Optional<String> guild) {
		return findAllOrdered(guildService.findByIdentifier(guild));
	}

	/**
	 * @see #findAllOrdered(Guild)
	 */
	public List<EventType> findAllOrdered(long guildId) {
		return findAllOrdered(guildService.findExisting(guildId));
	}

	/**
	 * Finds all global and guild specific {@link EventType}s
	 *
	 * @param guild to find event types for
	 * @return event types ordered by name
	 */
	private List<EventType> findAllOrdered(@NonNull Guild guild) {
		return eventTypeRepository.findByGuildNullOrGuildOrderByName(guild);
	}

	public void deleteUnused() {
		eventTypeRepository.deleteAllInBatch(eventTypeRepository.findAll()
				.stream().filter(eventType -> eventType.getEvents().isEmpty()).toList());
	}

	/**
	 * Adds or removed a {@link RequirementList} for a given {@link EventType}
	 *
	 * @param guildId           in which the event type and list are expected
	 * @param id                event type id
	 * @param requirementListId requirement list id
	 * @param listActive        true if requirement list should be activated, false if it should be deactivated
	 */
	public void setRequirementList(long guildId, long id, long requirementListId, boolean listActive) {
		final EventType eventType = eventTypeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		final RequirementList requirementList = requirementListService.find(requirementListId);
		if (eventType.getGuild().getId() != guildId || (requirementList.getGuild() != null && requirementList.getGuild().getId() != guildId)) {
			throw new ForbiddenException("Event type or requirement list does not belong to this guild");
		}
		if (listActive) {
			eventType.getRequirementList().add(requirementList);
		} else {
			eventType.getRequirementList().remove(requirementList);
		}
	}
}
