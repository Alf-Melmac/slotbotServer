package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.model.User;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.quartz.*;

/**
 * @author Alf
 * @since 07.08.2021
 */
@UtilityClass
public final class JobUtils {
	public static String buildJobNameForUserInEvent(User user, Event event) {
		return user.getId() + "_" + event.getId();
	}

	public static JobDetail buildJobDetails(Class<? extends Job> jobClass, @NonNull JobInfo info) {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(info.getName(), info);

		return JobBuilder
				.newJob(jobClass)
				.withIdentity(info.getName())
				.setJobData(jobDataMap)
				.build();
	}

	public static Trigger buildTrigger(@NonNull JobInfo info) {
		return TriggerBuilder
				.newTrigger()
				.withIdentity(info.getName())
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
				.startAt(info.getStart())
				.build();
	}

	public static JobKey createJobKey(String jobKey) {
		return new JobKey(jobKey);
	}

	public static JobInfo buildJobInfoForEvent(Event event, User user) {
		return JobInfo.builder()
				.name(buildJobNameForUserInEvent(user, event))
				.recipient(user.getId())
				.start(DateUtils.asDate(event.getDateTime().minusHours(1)))
				.message("**Erinnerung**: Das Event **" + event.getName() + "** geht in einer Stunde los.")
				.build();
	}
}
