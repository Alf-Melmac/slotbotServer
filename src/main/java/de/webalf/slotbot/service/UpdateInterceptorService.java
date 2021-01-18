package de.webalf.slotbot.service;

import de.webalf.slotbot.model.*;
import de.webalf.slotbot.service.bot.EventUpdateService;
import de.webalf.slotbot.util.bot.MessageHelper;
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
	private final MessageHelper messageHelper;

	/**
	 * Informs the discord bot about a deletion in an event
	 *
	 * @param entity that may be an event related object
	 */
	public void onDelete(Object entity) {
		update(getEvent(entity));
	}

	public void update(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
		update(getEvent(entity, currentState, previousState, propertyNames));
	}

	private void update(Event event) {
		if (event == null || !event.isPrinted()) {
			return;
		}

		eventUpdateService.update(event);
	}

	/**
	 * Returns the associated event if the entity is a {@link Event}, {@link Squad} or {@link Slot}
	 * The reserve is removed only in conjunction with another "slot-creating" action. Therefore, no onDelete needs to be made in this case
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

	private Event getEvent(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
		if (entity instanceof Event) {
			return (Event) entity;
		} else if (entity instanceof Squad) {
			final Squad squad = (Squad) entity;
			if (!squad.isReserve()) {
				return squad.getEvent();
			}
		} else if (entity instanceof Slot) {
			final Slot slot = (Slot) entity;
			final Event event = slot.getSquad().getEvent();
			for (int i = 0; i < propertyNames.length; i++) {
				if (propertyNames[i].equals(Slot_.USER)) {
					eventUpdateService.informAboutSlotChange(event, slot, (User) currentState[i], (User) previousState[i]);
					break;
				}
			}
			return event;
		}
		return null;
	}

	public void onSave(Object entity) {
		if (entity instanceof Squad) {
			final Squad squad = (Squad) entity;
			if (!squad.isReserve()) {
				update(squad.getEvent());
			}
		} else if (entity instanceof Slot) {
			final Slot slot = (Slot) entity;
			if (!slot.isInReserve()) {
				update(slot.getEvent());
			}
		} else if (entity instanceof User) {
			final User user = (User) entity;
			if (!user.isDefaultUser()) {
				messageHelper.sendDmToRecipient(user, "Schön dich bei AmB begrüßen zu dürfen. Falls du vor deiner Teilnahme einen Technikcheck machen möchtest, oder sonstige Fragen hast, melde dich bitte bei <@327385716977958913>. Ansonsten wünschen wir dir viel Spaß!");
			}
		}
	}
}
