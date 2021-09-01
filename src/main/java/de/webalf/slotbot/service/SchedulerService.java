package de.webalf.slotbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SchedulerService {
	private ScheduledExecutorService scheduler;

	@PostConstruct
	private void init() {
		scheduler = Executors.newScheduledThreadPool(1);
	}

	/**
	 * Schedules the given {@link Runnable} to be executed after the given delay
	 *
	 * @param runnable task to execute after given delay
	 * @param delay    in minutes
	 * @return scheduled task
	 */
	public ScheduledFuture<?> schedule(Runnable runnable, long delay) {
		return scheduler.schedule(runnable, delay, TimeUnit.MINUTES);
	}

	@PreDestroy
	private void cleanUp() {
		scheduler.shutdown();
	}
}
