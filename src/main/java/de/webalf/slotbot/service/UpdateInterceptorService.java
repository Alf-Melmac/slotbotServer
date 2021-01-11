package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.service.bot.EventUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UpdateInterceptorService {
	private final EventUpdateService eventUpdateService;

	/**
	 * Informs the slotbot about an update in one event
	 *
	 * @param entity that may be an event related object
	 */
	public void update(Object entity) {
		final Event event = getEvent(entity);
		if (event == null || !event.isPrinted()) {
			return;
		}

		eventUpdateService.update(event);
	}

	/**
	 * Returns the associated event if the entity is a {@link Event}, {@link Squad} or {@link Slot}
	 *
	 * @param entity to get the event for
	 * @return associated event or null
	 */
	private Event getEvent(Object entity) {
		if (entity instanceof Event) {
			return (Event) entity;
		} else if (entity instanceof Squad) {
			final Squad squad = (Squad) entity;
			if (!squad.isReserve()) {
				return squad.getEvent();
			}
		} else if (entity instanceof Slot) {
			final Slot slot = (Slot) entity;
			if (!slot.isInReserve()) {
				return slot.getSquad().getEvent();
			}
		}
		return null;
	}
}
