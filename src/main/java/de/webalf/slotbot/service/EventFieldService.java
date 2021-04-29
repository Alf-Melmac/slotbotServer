package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.dtos.EventFieldDto;
import de.webalf.slotbot.repository.EventFieldRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.eventfield.Arma3FieldUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventFieldService {
	private final EventFieldRepository eventFieldRepository;

	void updateEventDetails(@NonNull List<EventFieldDto> detailsDtos, @NonNull Event event) {
		List<EventField> existingDetails = event.getDetails();
		if (existingDetails != null) {
			existingDetails.clear();
		} else {
			event.setDetails(new ArrayList<>());
			existingDetails = event.getDetails();
		}

		List<EventField> eventDetails = new ArrayList<>();
		detailsDtos.forEach(eventFieldDto -> eventDetails.add(updateEventField(eventFieldDto, event)));
		existingDetails.addAll(eventDetails);
	}

	/**
	 * Updates a event field with the given values identified by its id
	 * (!) Event can not be changed
	 *
	 * @param dto   with new values
	 * @param event is required when a new field must be created
	 * @return updated or new event field
	 */
	private EventField updateEventField(@NonNull EventFieldDto dto, @NonNull Event event) {
		EventField eventField = eventFieldRepository.findById(dto.getId()).orElseGet(() -> EventField.builder().event(event).build());

		DtoUtils.ifPresent(dto.getTitle(), eventField::setTitle);
		DtoUtils.ifPresent(dto.getText(), eventField::setText);

		return eventField;
	}

	/**
	 * Returns the default {@link EventField}s for the given {@link EventType#name}
	 *
	 * @param eventTypeName name of event type
	 * @return matching default fields (including only field titles)
	 */
	public List<EventFieldDefaultDto> getDefault(String eventTypeName) {
		switch (eventTypeName) {
			case Arma3FieldUtils.EVENT_TYPE_NAME:
				return Arma3FieldUtils.FIELDS;
			default:
				return Collections.emptyList();
		}
	}
}
