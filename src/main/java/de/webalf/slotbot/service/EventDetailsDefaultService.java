package de.webalf.slotbot.service;

import de.webalf.slotbot.model.EventDetailDefault;
import de.webalf.slotbot.model.EventDetailsDefault;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.EventDetailDefaultDto;
import de.webalf.slotbot.repository.EventDetailDefaultRepository;
import de.webalf.slotbot.repository.EventDetailsDefaultRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public EventDetailsDefault findByName(String eventTypeName, Optional<String> guild) {
		return eventDetailsDefaultRepository.findByEventTypeNameAndGuild(eventTypeName, guildService.findByIdentifier(guild))
				.orElse(null);
	}

	public EventDetailsDefault updateDefaults(String eventTypeName, List<EventDetailDefaultDto> eventDetails, long guildId) {
		final Guild guild = guildService.findExisting(guildId);
		if (eventDetails.isEmpty()) {
			eventDetailsDefaultRepository.deleteByEventTypeNameAndGuild(eventTypeName, guild);
			return null;
		}

		final EventDetailsDefault detailsDefault = eventDetailsDefaultRepository.findByEventTypeNameAndGuild(eventTypeName, guild)
				.orElseGet(() -> EventDetailsDefault.builder()
						.eventTypeName(eventTypeName)
						.guild(guild)
						.eventFieldDefaults(new ArrayList<>())
						.build());

		DtoUtils.ifPresentObject(eventDetails, eventDetailDtos -> {
			final List<EventDetailDefault> existingDefaults = detailsDefault.getEventFieldDefaults();
			existingDefaults.clear();

			final List<EventDetailDefault> detailDefaults = new ArrayList<>();
			eventDetails.forEach(eventDetail -> detailDefaults.add(updateOrCreateEventField(eventDetail, detailsDefault)));
			existingDefaults.addAll(detailDefaults);
		});

		return eventDetailsDefaultRepository.save(detailsDefault);
	}

	private EventDetailDefault updateOrCreateEventField(@NonNull EventDetailDefaultDto dto, @NonNull EventDetailsDefault eventDetailsDefault) {
		EventDetailDefault eventDetailDefault = eventDetailDefaultRepository.findById(dto.getId())
				.orElseGet(() -> EventDetailDefault.builder().eventDetailsDefault(eventDetailsDefault).build());

		DtoUtils.ifPresent(dto.getTitle(), eventDetailDefault::setTitle);
		DtoUtils.ifPresentObject(dto.getType(), eventDetailDefault::setType);
		DtoUtils.ifPresentObject(dto.getSelection(), eventDetailDefault::setSelection);
		final String text = dto.getText();
		eventDetailDefault.setText(StringUtils.isEmpty(text) ? null : text); //Always set the text so that it can be emptied

		return eventDetailDefault;
	}
}
