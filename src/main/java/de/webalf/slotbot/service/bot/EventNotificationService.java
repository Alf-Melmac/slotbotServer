package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.service.job.EventNotificationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.webalf.slotbot.util.JobUtils.buildJobInfoForEvent;
import static de.webalf.slotbot.util.JobUtils.buildJobNameForUserInEvent;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventNotificationService {
	private final SchedulerService schedulerService;
	private final EventService eventService;

	/**
	 * Deletes all existing jobs and reschedules event notifications
	 */
	public void rebuildAllNotifications() {
		schedulerService.deleteAllJobs();
		final List<Event> allInFuture = eventService.findAllInFuture();
		log.info("Building notifications for {} events.", allInFuture.size());
		allInFuture.forEach(event -> event.getSquadList().forEach(squad -> squad.getSlotList().forEach(slot -> {
					if (slot.isEmpty()) {
						return;
					}
					schedulerService.schedule(EventNotificationJob.class, buildJobInfoForEvent(event, slot.getUser()));
				}))
		);
	}

	/**
	 * Updates existing notification or create new notification for given event and user
	 *
	 * @param event about which notification is to be sent
	 * @param user  to notify
	 */
	public void updateNotification(Event event, User user) {
		final String jobName = buildJobNameForUserInEvent(user, event);
		final JobInfo jobInfo = buildJobInfoForEvent(event, user);
		schedulerService.getRunningJob(jobName).ifPresentOrElse(
				job -> schedulerService.updateJob(jobName, job, jobInfo),
				() -> schedulerService.schedule(EventNotificationJob.class, jobInfo));
	}

	/**
	 * Removes the notification for the given event and user combination
	 *
	 * @param event about which no more the notification should no longer be sent
	 * @param user  to notify
	 */
	public void removeNotification(Event event, User user) {
		schedulerService.deleteJob(buildJobNameForUserInEvent(user, event));
	}
}
