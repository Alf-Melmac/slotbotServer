package de.webalf.slotbot.service.update;

import de.webalf.slotbot.model.*;
import de.webalf.slotbot.model.event.EventMetadataUpdateEvent;
import de.webalf.slotbot.model.event.SlotUserChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.spi.PersistentList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Service
@RequiredArgsConstructor
@Slf4j
class UpdateInterceptorService {
	private final EventUpdater eventUpdater;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Informs the discord bot about a deletion in an event
	 *
	 * @param entity that may be an event related object
	 */
	void onDelete(Object entity) {
		eventUpdater.update(getEvent(entity));
	}

	/**
	 * Informs the discord bot about an update in an event
	 *
	 * @param entity        that may be an event related object
	 * @param currentState  of the entity
	 * @param previousState of the entity
	 * @param propertyNames of the updated entity properties
	 */
	void update(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
		eventUpdater.update(getEvent(entity, currentState, previousState, propertyNames));
	}

	/**
	 * Returns the associated event if the entity is a {@link Slot}.
	 * The reserve is removed only in conjunction with another "slot-creating" action. Therefore, no onDelete needs to be made in this case.
	 * <p>
	 * This method is called when a
	 * <ul>
	 *     <li>slot is deleted</li>
	 *     <li>squad is deleted</li>
	 * </ul>
	 *
	 * @param entity to get the event for
	 * @return associated event update or null
	 */
	private EventUpdateSetting getEvent(Object entity) {
		if (entity instanceof final Slot slot) {
			if (!slot.isInReserve()) {
				log.trace("Update onDelete slot");
				return EventUpdateSetting.builder()
						.event(slot.getSquad().getEvent())
						.embed(false)
						.slotlist(true)
						.build();
			}
		}
		return null;
	}

	/**
	 * Returns the associated event if the entity is a {@link Event} or {@link Slot}.
	 * <p>
	 * This method is called when
	 * <ul>
	 *     <li>any event field is changed</li>
	 *     <li>squad is renamed</li>
	 *     <li>slot<ul>
	 *         <li>renamed</li>
	 *          <li>slotted</li>
	 *          <li>unslotted</li>
	 *          <li>blocked</li>
	 *          <li>switched</li>
	 *          <li>updated</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * @see #eventUpdate(Object[], String[], Event)
	 * @see #slotUpdate(Object[], Object[], String[], Slot, Event)
	 */
	private EventUpdateSetting getEvent(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
		if (entity instanceof final Event event) {
			eventUpdate(previousState, propertyNames, event);
			log.trace("Update update event");
			return EventUpdateSetting.builder()
					.event(event)
					.embed(true)
					.slotlist(false)
					.build();
		} else if (entity instanceof final Squad squad) {
			log.trace("Update update squad");
			return EventUpdateSetting.builder()
					.event(squad.getEvent())
					.embed(false)
					.slotlist(true)
					.build();
		} else if (entity instanceof final Slot slot) {
			final Event event = slot.getSquad().getEvent();
			slotUpdate(currentState, previousState, propertyNames, slot, event);
			log.trace("Update update slot");
			return EventUpdateSetting.builder()
					.event(event)
					.embed(false)
					.slotlist(true)
					.build();
		}
		return null;
	}

	/**
	 * Triggers {@link EventMetadataUpdateEvent} if necessary due to changes in an {@link Event}.
	 */
	private void eventUpdate(Object[] previousState, String[] propertyNames, Event event) {
		propertyLoop:
		for (int i = 0; i < propertyNames.length; i++) {
			switch (propertyNames[i]) {
				case Event_.NAME -> {
					final String oldName = (String) previousState[i];
					final String newName = event.getName();
					if (!oldName.equals(newName)) {
						eventPublisher.publishEvent(new EventMetadataUpdateEvent(event.getId()));
						break propertyLoop;
					}
				}
				case Event_.HIDDEN -> {
					final boolean oldHiddenState = (boolean) previousState[i];
					final boolean newHiddenState = event.isHidden();
					if (oldHiddenState != newHiddenState) {
						eventPublisher.publishEvent(new EventMetadataUpdateEvent(event.getId()));
						break propertyLoop;
					}
				}
				case Event_.DATE_TIME -> {
					final LocalDateTime oldEventDateTime = (LocalDateTime) previousState[i];
					final LocalDateTime newEventDateTime = event.getDateTime();
					if (!oldEventDateTime.isEqual(newEventDateTime)) {
						eventPublisher.publishEvent(new EventMetadataUpdateEvent(event.getId()));
						break propertyLoop;
					}
				}
				default -> {
					// continue searching for handled changed properties
				}
			}
		}
	}

	/**
	 * Triggers an {@link SlotUserChangedEvent} if a {@link Slot} user changed.
	 */
	private void slotUpdate(Object[] currentState, Object[] previousState, String[] propertyNames, Slot slot, Event event) {
		if (!slot.isInReserve()) {
			for (int i = 0; i < propertyNames.length; i++) {
				if (propertyNames[i].equals(Slot_.USER) && !Objects.equals(currentState[i], previousState[i])) {
					eventPublisher.publishEvent(SlotUserChangedEvent.builder()
							.event(event)
							.slot(slot)
							.currentUser((User) currentState[i])
							.previousUser((User) previousState[i])
							.build());
					break;
				}
			}
		}
	}

	/**
	 * Triggers an {@link EventUpdater#update(EventUpdateSetting)} if a {@link Squad} or {@link EventField} changed.
	 * <p>
	 * This method is called when
	 * <ul>
	 *     <li>squad<ul>
	 *         <li>created</li>
	 *         <li>renamed</li>
	 *         <li>reordered</li>
	 *         <li>slot changed (added, removed, renamed, blocked, reservation change)</li>
	 *         <li>reservation change</li>
	 *     </ul></li>
	 *     <li>event field<ul>
	 *         <li>created</li>
	 *         <li>removed</li>
	 *         <li>reordered</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * @param collection updated collection
	 */
	void onCollectionUpdate(Object collection) {
		if (collection instanceof final PersistentList<?> persistentList) {
			if (persistentList.isEmpty()) return;
			final Object el = persistentList.get(0);

			if (el instanceof final Squad squad) {
				if (!squad.isReserve()) {
					log.trace("Update onCollectionUpdate squad");
					eventUpdater.update(EventUpdateSetting.builder().event(squad.getEvent()).embed(false).slotlist(true).build());
				}
			} else if (el instanceof final EventField eventField) {
				log.trace("Update onCollectionUpdate event field");
				eventUpdater.update(EventUpdateSetting.builder().event(eventField.getEvent()).embed(true).slotlist(false).build());
			}
		}
	}
}
