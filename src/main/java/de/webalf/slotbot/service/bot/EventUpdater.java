package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.service.SchedulerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class EventUpdater {
	private final SchedulerService schedulerService;
	private final EventUpdateService eventUpdateService;

	private static final Map<Long, EventUpdate> SCHEDULED_UPDATE = new ConcurrentHashMap<>();

	private record EventUpdate(Future<?> future, boolean embed, boolean slotlist) {
		/**
		 * Creates a new event update setting based on the current scheduled update and the given setting.
		 * This adds embed and slotlist updates to update everything at once.
		 */
		private EventUpdateSetting createSetting(@NonNull EventUpdateSetting setting) {
			log.trace("Creating new event update setting {} - {} : {}", setting.event().getId(), embed || setting.embed(), slotlist || setting.slotlist());
			return EventUpdateSetting.builder()
					.event(setting.event())
					.embed(embed || setting.embed())
					.slotlist(slotlist || setting.slotlist())
					.build();
		}
	}

	/**
	 * Schedules an update for the given event (with settings) if it is {@link Event#isAssigned() assigned}.
	 */
	public void update(EventUpdateSetting eventUpdateSetting) {
		if (eventUpdateSetting == null) {
			return;
		}
		final Event event = eventUpdateSetting.event();
		if (event == null || !event.isAssigned()) {
			return;
		}
		final long eventId = event.getId();

		final EventUpdateSetting newSetting;
		final EventUpdate update = SCHEDULED_UPDATE.get(eventId);
		if (update != null) {
			final Future<?> future = update.future();
			if (future != null && !future.isDone()) {
				log.trace("Cancel scheduled update for event {}", eventId);
				future.cancel(false);
			}
			newSetting = update.createSetting(eventUpdateSetting);
		} else {
			newSetting = eventUpdateSetting;
		}
		SCHEDULED_UPDATE.put(eventId, new EventUpdate(schedulerService.schedule(
				() -> eventUpdateService.update(newSetting),
				() -> SCHEDULED_UPDATE.remove(eventId),
				5, TimeUnit.SECONDS),
				newSetting.embed(),
				newSetting.slotlist()));
	}
}
