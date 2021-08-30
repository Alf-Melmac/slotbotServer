package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.NotificationSettingsService;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.service.job.EventNotificationJob;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.JobUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.JobUtils.buildJobDetails;
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
	private final NotificationSettingsService notificationSettingsService;

	@PostConstruct
	private void init() {
		log.error("--- PostConstruct ENS");
		schedulerService.initJob(buildJobDetails(EventNotificationJob.class));
	}

	/**
	 * Deletes all existing jobs and reschedules event notifications
	 */
	public void rebuildAllNotifications() {
		schedulerService.unscheduleAll();
		final List<Event> allInFuture = eventService.findAllInFuture();
		log.info("Building notifications for {} events.", allInFuture.size());
		allInFuture.forEach(event -> event.getAllParticipants().forEach(user ->
				schedulerService.schedule(buildJobInfosForUserInEvent(user, event))));
	}

	/**
	 * Updates existing notification or create new notification for given event and user
	 *
	 * @param event about which notification is to be sent
	 * @param user  to notify
	 */
	public void updateOrCreateNotification(Event event, User user) {
		 //TODO



		final String jobName = buildJobNameForUserInEvent(user, event);
		final List<JobInfo> jobInfos = buildJobInfosForUserInEvent(user, event);
		jobInfos.forEach(jobInfo ->
				schedulerService.getRunningJob(jobName).ifPresentOrElse(
						job -> schedulerService.updateJob(jobName, job, jobInfo),
						() -> schedulerService.schedule(jobInfo)));
	}

	public void updateNotification(Event event) {
		schedulerService.getTriggersOfJobsInGroup(Long.toString(event.getId()))
				.forEach(trigger -> schedulerService.reschedule(trigger, DateUtils.asDate(event.getDateTime().minusHours(1))));
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

	private List<JobInfo> buildJobInfosForUserInEvent(User user, Event event) {
		return notificationSettingsService.findSettings(user, event).stream()
				.map(notificationSetting ->
						JobUtils.buildJobInfoForEvent(event, user, notificationSetting.getNotificationTime(event.getDateTime())))
				.collect(Collectors.toUnmodifiableList());
	}

	public void test() {
		schedulerService.test();
	}
}
