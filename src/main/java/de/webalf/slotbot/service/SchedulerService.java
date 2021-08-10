package de.webalf.slotbot.service;

import de.webalf.slotbot.model.JobInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.JobUtils.*;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class SchedulerService {
	private final Scheduler scheduler;

	public void schedule(Class<? extends Job> jobClass, JobInfo info) {
		final JobDetail jobDetail = buildJobDetails(jobClass, info);
		final Trigger trigger = buildTrigger(info);

		try {
			if (log.isTraceEnabled()) {
				log.trace("Scheduling {} named '{}'", jobClass.getSimpleName(), info.getName());
			}
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			log.error("Failed to schedule job", e);
		}
	}

	public List<JobInfo> getAllRunningJobs() {
		try {
			return scheduler.getJobKeys(GroupMatcher.anyGroup())
					.stream()
					.map(jobKey -> {
						final JobDetail jobDetail = getJobDetail(jobKey);
						if (jobDetail == null) {
							log.warn("Missing job detail '{}'", jobKey.getName());
							return null;
						}
						return (JobInfo) jobDetail.getJobDataMap().get(jobKey.getName());
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toUnmodifiableList());
		} catch (SchedulerException e) {
			log.error("Failed to get all running jobs");
			return Collections.emptyList();
		}
	}

	public Optional<JobDetail> getRunningJob(String jobKey) {
		return Optional.ofNullable(getJobDetail(createJobKey(jobKey)));
	}

	public void updateJob(String jobKey, JobDetail jobDetail, JobInfo info) {
		jobDetail.getJobDataMap().put(jobKey, info);
		try {
			scheduler.addJob(jobDetail, true, true);
		} catch (SchedulerException e) {
			log.error("Failed to update job '{}'", jobKey, e);
		}
	}

	public void deleteJob(String jobKey) {
		try {
			scheduler.deleteJob(createJobKey(jobKey));
		} catch (SchedulerException e) {
			log.error("Failed to delete job '{}'", jobKey, e);
		}
	}

	public void deleteAllJobs() {
		try {
			scheduler.deleteJobs(new ArrayList<>(scheduler.getJobKeys(GroupMatcher.anyGroup())));
		} catch (SchedulerException e) {
			log.error("Failed to get all job keys to delete them", e);
		}
	}

	private JobDetail getJobDetail(JobKey jobKey) {
		try {
			return scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {
			log.error("Failed to get job '{}'", jobKey, e);
		}
		return null;
	}

	@PostConstruct
	private void init() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("Failed to start scheduler", e);
		}
	}

	@PreDestroy
	private void cleanUp() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			log.error("Failed to shutdown scheduler", e);
		}
	}
}
