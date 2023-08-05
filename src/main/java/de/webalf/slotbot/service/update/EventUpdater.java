package de.webalf.slotbot.service.update;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.model.event.EventUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Delays and collects all updates to an event. The event is updated 5 seconds after the last update was received.
 *
 * @author Alf
 * @since 10.05.2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
class EventUpdater {
	private final SchedulerService schedulerService;
	private final ApplicationEventPublisher eventPublisher;

	private static final Map<Long, EventUpdate> SCHEDULED_UPDATE = new ConcurrentHashMap<>();

	private record EventUpdate(Future<?> future, boolean embed, boolean slotlist) {}

	/**
	 * Schedules an update for the given event (with settings) if it is {@link Event#isAssigned() assigned}.
	 */
	void update(EventUpdateSetting eventUpdateSetting) {
		if (eventUpdateSetting == null) {
			return;
		}
		final Event event = eventUpdateSetting.event();
		if (event == null || !event.isAssigned()) {
			return;
		}
		final long eventId = event.getId();

		final EventUpdate update = SCHEDULED_UPDATE.get(eventId);
		boolean embedChanged;
		boolean slotlistChanged;
		if (update != null) {
			final Future<?> future = update.future();
			if (future != null && !future.isDone()) {
				log.trace("Cancel scheduled update for event {}", eventId);
				future.cancel(false);
			}
			embedChanged = update.embed() || eventUpdateSetting.embed();
			slotlistChanged = update.slotlist() || eventUpdateSetting.slotlist();
		} else {
			embedChanged = eventUpdateSetting.embed();
			slotlistChanged = eventUpdateSetting.slotlist();
		}
		SCHEDULED_UPDATE.put(eventId, new EventUpdate(schedulerService.schedule(
				() -> eventPublisher.publishEvent(EventUpdateEvent.builder()
						.event(eventId)
						.embedChanged(embedChanged)
						.slotlistChanged(slotlistChanged)
						.build()),
				() -> SCHEDULED_UPDATE.remove(eventId),
				5, TimeUnit.SECONDS),
				embedChanged,
				slotlistChanged));
	}
}
