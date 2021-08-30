package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.service.bot.EventNotificationService;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.quartz.*;

import java.time.LocalDateTime;

import static net.dv8tion.jda.api.utils.TimeFormat.RELATIVE;

/**
 * @author Alf
 * @since 07.08.2021
 */
@UtilityClass
public final class JobUtils {
	public static String buildJobNameForUserInEvent(User user, Event event) {
		return event.getId() + "_" + user.getId();
	}

	public static JobDetail buildJobDetails(@NonNull Class<? extends Job> jobClass) {
		return JobBuilder
				.newJob(jobClass)
				.withIdentity(jobClass.getSimpleName())
				.build();
	}

	public static Trigger buildTrigger(@NonNull JobInfo info) {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(info.getName(), info);

		return TriggerBuilder
				.newTrigger()
				.withIdentity(info.getName(), info.getGroup())
				.forJob(info.getJobName())
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
				.startAt(info.getStart())
				.usingJobData(jobDataMap)
				.build();
	}

	public static JobKey createJobKey(String jobKey) {
		return new JobKey(jobKey);
	}

	public static JobInfo buildJobInfoForEvent(Event event, User user, LocalDateTime start) {
		return JobInfo.builder()
				.name(buildJobNameForUserInEvent(user, event))
				.group(Long.toString(event.getId()))
				.jobName(EventNotificationService.class.getSimpleName())
				.recipient(user.getId())
				.start(DateUtils.asDate(start))
				//TODO Check message
				.message("**Erinnerung**: Das Event **" + event.getName() + "** geht in " + RELATIVE.format(DateUtils.getDateTimeZoned(event.getDateTime())) + " los.")
				.build();
	}
}
