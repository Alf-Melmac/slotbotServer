package de.webalf.slotbot.service;

import de.webalf.slotbot.model.JobInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.JobUtils.buildTrigger;
import static de.webalf.slotbot.util.JobUtils.createJobKey;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class SchedulerService {
	private final Scheduler scheduler;

	//NEW
	@PostConstruct
	private void init() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("Failed to start scheduler", e);
		}
	}

	//NEW
	public void initJob(JobDetail jobDetail) {
		try {
			scheduler.addJob(jobDetail, true, true);
		} catch (SchedulerException e) {
			log.error("Failed to init job", e);
		}
	}

	//NEW
	public void schedule(JobInfo info) {
		final Trigger trigger = buildTrigger(info);
		try {
			if (log.isTraceEnabled()) {
				log.trace("Scheduling trigger for {} named '{}'", info.getJobName(), info.getName());
			}
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			log.error("Failed to schedule job", e);
		}
	}

	//NEW
	public void schedule(@NonNull Iterable<? extends JobInfo> infos) {
		StreamSupport.stream(infos.spliterator(), true)
						.forEach(this::schedule);
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
			log.error("Failed to get all running jobs", e);
			return Collections.emptyList();
		}
	}

	//NEW
	public List<JobInfo> getAllTriggers() {
		try {
			return scheduler.getTriggerKeys(GroupMatcher.anyGroup())
					.stream()
					.map(triggerKey -> {
						final Trigger trigger = getTrigger(triggerKey);
						if (trigger == null) {
							log.warn("Missing trigger '{}'", triggerKey.getName());
							return null;
						}
						return (JobInfo) trigger.getJobDataMap().get(triggerKey.getName());
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toUnmodifiableList());
		} catch (SchedulerException e) {
			log.error("Failed to get all triggers", e);
			return Collections.emptyList();
		}
	}

	public List<Trigger> getTriggersOfJobsInGroup(String groupName) {
		try {
			return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))
					.stream().flatMap(jobKey -> {
						try {
							return scheduler.getTriggersOfJob(jobKey).stream();
						} catch (SchedulerException e) {
							log.error("Failed to get trigger of job {}", jobKey.getName(), e);
							return Stream.empty();
						}
					})
					.collect(Collectors.toUnmodifiableList());
		} catch (SchedulerException e) {
			log.error("Failed to get all running jobs", e);
			return Collections.emptyList();
		}
	}

	public void reschedule(@NonNull Trigger oldTrigger, Date newStart) {
		final Trigger newTrigger = oldTrigger.getTriggerBuilder()
				.startAt(newStart)
				.build();

		try {
			scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
		} catch (SchedulerException e) {
			log.error("Failed to reschedule job {}", oldTrigger.getJobKey().getName(), e);
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

	//NEW
	public void unscheduleAll() {
		try {
			scheduler.unscheduleJobs(new ArrayList<>(scheduler.getTriggerKeys(GroupMatcher.anyGroup())));
		} catch (SchedulerException e) {
			log.error("Failed to get all triggers to unschedule all jobs", e);
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

	//NEW
	private Trigger getTrigger(TriggerKey triggerKey) {
		try {
			return scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			log.error("Failed to get trigger '{}'", triggerKey.getName(), e);
		}
		return null;
	}

	//NEW
	@PreDestroy
	private void cleanUp() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			log.error("Failed to shutdown scheduler", e);
		}
	}

	@SneakyThrows
	public void test() {
		Consumer<JobKey> jobKeyConsumer = jobKey -> log.error("Name " + jobKey.getName() + " Gorup: " + jobKey.getGroup());

		scheduler.getJobKeys(GroupMatcher.jobGroupEquals("1483"))
				.forEach(jobKeyConsumer);
	}
}
