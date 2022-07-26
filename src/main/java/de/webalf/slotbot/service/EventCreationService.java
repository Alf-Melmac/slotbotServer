package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.website.event.creation.EventPostAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.website.event.creation.EventPostDto;
import de.webalf.slotbot.repository.EventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventCreationService {
	private final EventRepository eventRepository;
	private final EventPostAssembler eventAssembler;

	/**
	 * Creates a new event with values from the {@link EventDto}
	 * {@link Event#setOwnerGuild(Guild)} is set by current request uri ({@link GuildService#getOwnerGuild(AbstractEventDto)})
	 *
	 * @param eventDto new event
	 * @return saved new event
	 */
	public Event createEvent(@NonNull EventPostDto eventDto) {
		Event event = eventAssembler.fromDto(eventDto);

		event.validate();

		return eventRepository.save(event);
	}

}
