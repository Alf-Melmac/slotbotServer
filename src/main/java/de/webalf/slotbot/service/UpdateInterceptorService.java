package de.webalf.slotbot.service;

import de.webalf.slotbot.model.*;
import de.webalf.slotbot.service.bot.EventUpdateService;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
	 * Informs the discord bot about an update in an event
	 *
	 * @param entity that may be an event related object
	 */
	public void update(Object entity) {
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
					informAboutSlotChange(slot, (User) currentState[i], (User) previousState[i], event);
					break;
				}
			}
			return event;
		}
		return null;
	}

	private void informAboutSlotChange(Slot slot, User currentUser, User previousUser, @NonNull Event event) {
		final String eventDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(event.getDateTime().toLocalDate());
		if (currentUser != null && !currentUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(currentUser, "Du bist im Event **" + event.getName() + "** am " + eventDate + " nun auf dem Slot " + slot.getNumber() + " *" + slot.getName() + "* eingetragen.");
		} else if (previousUser != null && !previousUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(previousUser, "Du bist nun vom Event **" + event.getName() + "** am " + eventDate + " ausgetragen.");
		}

	}
}
