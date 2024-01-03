package de.webalf.slotbot.service;

import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.repository.EventDetailsDefaultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventDetailsDefaultService {
	private final EventDetailsDefaultRepository eventDetailsDefaultRepository;
	private final GuildService guildService;

	public EventDetailsDefault getDefault(String eventTypeName) {
		return eventDetailsDefaultRepository.findByEventTypeNameAndGuild(eventTypeName, guildService.findCurrentNonNullGuild())
				.orElse(null);
	}
}
