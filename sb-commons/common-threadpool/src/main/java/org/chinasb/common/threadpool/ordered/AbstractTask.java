package org.chinasb.common.threadpool.ordered;

import org.chinasb.common.threadpool.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 普通任务抽象
 * 
 * @author zhujuan
 *
 */
public abstract class AbstractTask implements Task {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 任务队列
	 */
	protected final TaskQueue taskQueue;

	public AbstractTask(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}
	
	/**
	 * 获取任务队列
	 * @return
	 */
	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public void execute() {
		long startTime = System.currentTimeMillis();
		run();
		long endTime = System.currentTimeMillis();
		long interval = endTime - startTime;
		if (interval >= 1000) {
			logger.warn("Execute task : " + this.toString() + ", interval : " + interval + ", task size : " + taskQueue.size());
		}
	}

	public abstract void run();
}
