package org.chinasb.common.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指令执行队列
 * @author zhujuan
 *
 */
public class DefaultCommandActionQueue implements CommandActionQueue {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultCommandActionQueue.class);
	private final Executor executor;
	private final ScheduledExecutorService delayExecutor;

	public DefaultCommandActionQueue(Executor executor,
			ScheduledExecutorService delayExecutor) {
		this.executor = executor;
		this.delayExecutor = delayExecutor;
	}

	@Override
	public void submit(final Runnable command) {
		if (command == null)
			return;
		executor.execute(command);
	}

	@Override
	public void submit(final Runnable command, int delay) {
		if (command == null)
			return;
		delayExecutor.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					submit(command);
				} catch (Throwable t) {
					LOGGER.error("DELAY EXECUTOR SUBMIT COMMAND ERROR", t);
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}
}
