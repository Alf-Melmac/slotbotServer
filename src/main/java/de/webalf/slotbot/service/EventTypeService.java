package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.repository.EventTypeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventTypeService {
	private final EventTypeRepository eventTypeRepository;

	public EventType find(@NonNull EventTypeDto eventTypeDto) {
		return eventTypeRepository.findEventTypeByNameAndColor(eventTypeDto.getName(), eventTypeDto.getColor())
				.orElseGet(() -> eventTypeRepository.save(EventTypeAssembler.fromDto(eventTypeDto)));
	}

	public List<EventType> findAll() {
		return eventTypeRepository.findAll();
	}
}
