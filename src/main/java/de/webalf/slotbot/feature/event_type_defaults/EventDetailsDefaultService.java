package de.webalf.slotbot.feature.event_type_defaults;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.feature.event_type_defaults.dto.EventDetailDefaultPostDto;
import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailDefault;
import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 03.01.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventDetailsDefaultService {
	private final EventDetailsDefaultRepository eventDetailsDefaultRepository;
	private final EventDetailDefaultRepository eventDetailDefaultRepository;
	private final GuildService guildService;
	private final EventTypeService eventTypeService;

	List<EventDetailDefault> findByEventTypeId(long guildId, long eventTypeId) {
		final EventType eventType = eventTypeService.find(eventTypeId, guildId);
		return eventDetailDefaultRepository.findAllByEventType(eventType);
	}

	List<EventDetailDefault> updateDefaults(long guildId, long eventTypeId, List<EventDetailDefaultPostDto> eventDetails) {
		final EventType eventType = eventTypeService.find(eventTypeId, guildId);

		final List<EventDetailDefault> detailDefaults = new ArrayList<>();
		eventDetails.forEach(dto -> detailDefaults.add(updateOrCreateDefault(dto, eventType)));

		//Delete no longer included defaults
		final Set<Long> updatedDefaults = detailDefaults.stream().map(EventDetailDefault::getId).collect(Collectors.toUnmodifiableSet());
		eventDetailDefaultRepository.deleteByEventTypeAndIdNotIn(eventType, updatedDefaults);

		return detailDefaults;
	}

	private EventDetailDefault updateOrCreateDefault(@NonNull EventDetailDefaultPostDto dto, @NonNull EventType eventType) {
		final EventDetailDefault detail;
		if (dto.id() == null) {
			detail = EventDetailDefault.builder()
					.eventType(eventType)
					.build();
		} else {
			detail = eventDetailDefaultRepository.findById(dto.id()).orElseThrow(ResourceNotFoundException::new);
		}
		DtoUtils.ifPresent(dto.title(), detail::setTitle);
		DtoUtils.ifPresentObject(dto.type(), detail::setType);
		DtoUtils.ifPresentObject(dto.selection(), detail::setSelection);
		final String text = dto.text();
		detail.setText(StringUtils.isEmpty(text) ? null : text); //Always set the text so that it can be emptied
		return eventDetailDefaultRepository.save(detail);
	}

	public EventDetailsDefault findByName(String eventTypeName, Optional<String> guild) {
		return findByName(eventTypeName, guildService.findByIdentifier(guild));
	}

	public EventDetailsDefault findByName(String eventTypeName, long guildId) {
		return findByName(eventTypeName, guildService.findExisting(guildId));
	}

	private EventDetailsDefault findByName(String eventTypeName, Guild guild) {
		return eventDetailsDefaultRepository.findByEventTypeNameAndGuild(eventTypeName, guild)
				.orElse(null);
	}

}
