package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.NotificationMap;
import de.webalf.slotbot.service.SchedulerService;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static de.webalf.slotbot.service.bot.EventUpdater.EventUpdateIdentifier.buildIdentifier;

/**
 * Collects all event updates within a second and executes them in a single operation to prevent spamming the bot
 *
 * @author Alf
 * @since 10.05.2023
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventUpdater {
	private final SchedulerService schedulerService;
	private final EventUpdateService eventUpdateService;

	private static final Map<EventUpdateIdentifier, ScheduledFuture<?>> SCHEDULED_UPDATE = new NotificationMap<>();

	@Builder
	record EventUpdateIdentifier(long eventId, boolean embed, boolean slotlist) {
		static EventUpdateIdentifier buildIdentifier(@NonNull EventUpdateSetting eventUpdateSetting) {
			return new EventUpdateIdentifier(eventUpdateSetting.event().getId(), eventUpdateSetting.embed(), eventUpdateSetting.slotlist());
		}
	}

	public void update(EventUpdateSetting eventUpdateSetting) {
		if (eventUpdateSetting == null) {
			return;
		}
		final Event event = eventUpdateSetting.event();
		if (event == null || !event.isAssigned()) {
			return;
		}

		SCHEDULED_UPDATE.computeIfAbsent(buildIdentifier(eventUpdateSetting), k -> schedulerService.schedule(
				() -> eventUpdateService.update(eventUpdateSetting),
				() -> SCHEDULED_UPDATE.remove(k),
				1, TimeUnit.SECONDS));
	}
}
