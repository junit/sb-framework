package org.chinasb.common.threadpool.ordered;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务队列
 * 
 * @author zhujuan
 *
 */
public class OrderedTaskQueue implements TaskQueue {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderedTaskQueue.class);

	private final Queue<AbstractTask> taskQueue;
	private final OrderedTaskQueueExecutor executor;
	private final Runnable runner;
	private boolean running;

	public OrderedTaskQueue(final OrderedTaskQueueExecutor executor) {
		if (executor == null) {
			throw new NullPointerException();
		}

		this.taskQueue = new LinkedList<AbstractTask>();
		this.executor = executor;
		this.runner = new Runnable() {
			public void run() {
				for (;;) {
					AbstractTask task = taskQueue.poll();
					if (task == null) {
						synchronized (taskQueue) {
							task = taskQueue.poll();
							if (task == null) {
								running = false;
								return;
							}
						}
					}
					try {
						task.execute();
					} catch (Throwable t) {
						LOGGER.error("Execute Task[{}] Caught unexpected Throwable[{}]", task, t);
					} finally {
						task.reset();
					}
				}
			}
		};
	}
	
	@Override
	public TaskQueue getTaskQueue() {
		return this;
	}

	@Override
	public boolean enqueue(AbstractTask task) {
		boolean result = false;
		synchronized (taskQueue) {
			result = taskQueue.offer(task);
			if (result) {
				if (!running) {
					running = true;
					executor.execute(runner);
				}
			} else {
				LOGGER.error("添加任务失败");
			}
		}
		return result;
	}

	@Override
	public void enDelayQueue(AbstractDelayTask delayTask) {
		executor.enDelayQueue(delayTask);
	}
	
	@Override
	public int size() {
		synchronized (taskQueue) {
			return taskQueue.size();
		}
	}
}
