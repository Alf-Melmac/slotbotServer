package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.api.event.EventTypeApiAssembler;
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

/**
 * @author Alf
 * @since 09.04.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventTypeService {
	private final EventTypeRepository eventTypeRepository;

	/**
	 * Finds a {@link EventType} by name and color of given dto or creates a new one with values from given dto for the
	 * given {@link Guild}
	 *
	 * @param eventTypeDto to find
	 * @return found eventType or new eventType
	 */
	public EventType find(@NonNull EventTypeDto eventTypeDto, Guild guild) {
		return eventTypeRepository.findEventTypeByNameAndColor(eventTypeDto.getName(), eventTypeDto.getColor())
				.orElseGet(() -> eventTypeRepository.save(EventTypeAssembler.fromDto(eventTypeDto, guild)));
	}

	/**
	 * Finds a {@link EventType} by name and color of given dto or creates a new one with values from given dto for the
	 * given {@link Guild}
	 *
	 * @param eventTypeDto to find
	 * @return found eventType or new eventType
	 */
	public EventType find(@NonNull EventTypeApiDto eventTypeDto, Guild guild) {
		return eventTypeRepository.findEventTypeByNameAndColor(eventTypeDto.getName(), eventTypeDto.getColor())
				.orElseGet(() -> eventTypeRepository.save(EventTypeApiAssembler.fromDto(eventTypeDto, guild)));
	}

	/**
	 * Finds all global and {@link GuildService#findCurrentNonNullGuild() guild specific} {@link EventType}s
	 *
	 * @param guild to find event types for
	 * @return event types ordered by name
	 */
	public List<EventType> findAllOrdered(@NonNull Guild guild) {
		return eventTypeRepository.findByGuildNullOrGuildOrderByName(guild);
	}

	public void deleteUnused() {
		eventTypeRepository.deleteAllInBatch(eventTypeRepository.findAll()
				.stream().filter(eventType -> eventType.getEvents().isEmpty()).toList());
	}
}
