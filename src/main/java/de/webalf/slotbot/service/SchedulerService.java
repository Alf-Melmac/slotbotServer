package de.webalf.slotbot.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Slf4j
public class SchedulerService {
	private ScheduledExecutorService scheduler;

	@PostConstruct
	private void init() {
		scheduler = Executors.newScheduledThreadPool(1);
	}

	/**
	 * @see #schedule(Runnable, Runnable, long)
	 */
	public ScheduledFuture<?> schedule(Runnable runnable, long delay) {
		return schedule(runnable, () -> {}, delay);
	}

	/**
	 * Schedules the given {@link Runnable} to be executed after the given delay
	 *
	 * @param runnable task to execute after given delay
	 * @param cleanup  task to execute after the action has been (successfully or not) executed
	 * @param delay    in minutes
	 * @return scheduled task
	 * @see #schedule(Runnable, Runnable, long, TimeUnit)
	 */
	public ScheduledFuture<?> schedule(Runnable runnable, Runnable cleanup, long delay) {
		return schedule(runnable, cleanup, delay, TimeUnit.MINUTES);
	}

	/**
	 * Schedules the given {@link Runnable} to be executed after the given delay. Errors are caught and logged.
	 *
	 * @param runnable task to execute after given delay
	 * @param cleanup  task to execute after the action has been (successfully or not) executed
	 * @param delay    after which the runnable is executed
	 * @param timeUnit unit of the delay
	 * @return scheduled task
	 */
	public ScheduledFuture<?> schedule(Runnable runnable, Runnable cleanup, long delay, TimeUnit timeUnit) {
		return scheduler.schedule(errorHandlingWrapper(runnable, cleanup), delay, timeUnit);
	}

	/**
	 * Wraps the given action in a try-catch block and executes the cleanup afterwards
	 *
	 * @param action  that may throw an exception
	 * @param cleanup that is executed after the action has been (successfully or not) executed
	 * @return wrapped runnable
	 */
	private static Runnable errorHandlingWrapper(Runnable action, Runnable cleanup) {
		return () -> {
			try {
				action.run();
			} catch (Exception e) {
				log.warn("Exception in scheduled task", e);
			} finally {
				cleanup.run();
			}
		};
	}

	@PreDestroy
	private void cleanUp() {
		scheduler.shutdown();
	}
}
