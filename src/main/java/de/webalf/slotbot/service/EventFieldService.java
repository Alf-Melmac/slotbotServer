package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldIdDto;
import de.webalf.slotbot.repository.EventFieldRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.EventUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 09.04.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventFieldService {
	private final EventFieldRepository eventFieldRepository;

	/**
	 * Updates the details of the given event to the given detail list
	 *
	 * @param detailsDtos new details
	 * @param event       to update
	 */
	void updateEventDetails(@NonNull List<MinimalEventFieldIdDto> detailsDtos, @NonNull Event event) {
		List<EventField> existingDetails = event.getDetails();
		if (existingDetails != null) {
			existingDetails.clear();
		} else {
			event.setDetails(new ArrayList<>());
			existingDetails = event.getDetails();
		}

		List<EventField> eventDetails = new ArrayList<>();
		detailsDtos.forEach(eventFieldDto -> eventDetails.add(updateOrCreateEventField(eventFieldDto, event)));
		existingDetails.addAll(eventDetails);

		event.validate();
	}

	/**
	 * Updates an event field with the given values identified by its id. If no field can be found, a new one is created.
	 * (!) Event can not be changed
	 *
	 * @param dto   with new values
	 * @param event is required when a new field must be created
	 * @return updated or new event field
	 */
	private EventField updateOrCreateEventField(@NonNull MinimalEventFieldIdDto dto, @NonNull Event event) {
		EventField eventField = eventFieldRepository.findById(dto.getId()).orElseGet(() -> EventField.builder().event(event).build());

		DtoUtils.ifPresent(dto.getTitle(), eventField::setTitle);
		DtoUtils.ifPresentOrEmpty(EventUtils.sanitize(dto.getText()), eventField::setText);

		return eventField;
	}
}
