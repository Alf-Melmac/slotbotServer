package de.webalf.slotbot.service.job;

import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventNotificationJob implements Job {
	private final MessageHelper messageHelper;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		final JobInfo info = (JobInfo) jobExecutionContext.getJobDetail().getJobDataMap()
				.get(jobExecutionContext.getJobDetail().getKey().getName());

		messageHelper.sendDmToRecipient(info.getRecipient(), info.getMessage());
	}
}
